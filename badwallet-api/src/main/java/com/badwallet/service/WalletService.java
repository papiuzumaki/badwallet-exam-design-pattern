package com.badwallet.service;

import com.badwallet.dto.*;
import com.badwallet.model.Wallet;
import com.badwallet.model.WalletTransaction;
import com.badwallet.model.TransactionType;
import com.badwallet.pattern.factory.DepositStrategyFactory;
import com.badwallet.pattern.observer.WalletEvent;
import com.badwallet.pattern.proxy.PaymentServiceProxy;
import com.badwallet.repository.WalletRepository;
import com.badwallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final DepositStrategyFactory depositStrategyFactory;
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentServiceProxy paymentServiceProxy;

    // Frais de retrait : 1% du montant, plafonné à 5000 CFA
    private static final double WITHDRAWAL_FEE_RATE = 0.01;
    private static final double WITHDRAWAL_FEE_CAP = 5000.0;

    @Async
    public void seedDatabase(int numWallets, int eventsPerWallet) {
        Random random = new Random();
        for (int i = 0; i < numWallets; i++) {
            String phone = "+22177" + String.format("%07d", i + 1);
            if (walletRepository.existsByPhoneNumber(phone)) continue;

            Wallet wallet = walletRepository.save(Wallet.builder()
                    .phoneNumber(phone)
                    .email("wallet" + i + "@badwallet.com")
                    .code("WLT-" + String.format("%07d", i + 1))
                    .currency("XOF")
                    .balance(0.0)
                    .build());

            for (int j = 0; j < eventsPerWallet; j++) {
                double amount = (random.nextInt(50) + 1) * 1000.0;
                double before = wallet.getBalance();
                wallet.setBalance(before + amount);
                transactionRepository.save(WalletTransaction.builder()
                        .wallet(wallet)
                        .type(TransactionType.DEPOSIT)
                        .amount(amount)
                        .fees(0.0)
                        .balanceBefore(before)
                        .balanceAfter(wallet.getBalance())
                        .description("Seed deposit")
                        .reference(UUID.randomUUID().toString())
                        .build());
            }
            walletRepository.save(wallet);
        }
    }

    @Transactional
    public Wallet createWallet(CreateWalletRequest req) {
        Wallet wallet = Wallet.builder()
                .phoneNumber(req.getPhoneNumber())
                .email(req.getEmail())
                .code(req.getCode())
                .currency(req.getCurrency() != null ? req.getCurrency() : "XOF")
                .balance(req.getInitialBalance() != null ? req.getInitialBalance() : 0.0)
                .build();
        return walletRepository.save(wallet);
    }

    public Page<Wallet> getAllWallets(Pageable pageable) {
        return walletRepository.findAll(pageable);
    }

    public Wallet getWalletByPhone(String phone) {
        return walletRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new RuntimeException("Wallet non trouvé : " + phone));
    }

    public Double getBalance(String phone) {
        return getWalletByPhone(phone).getBalance();
    }

    @Transactional
    public WalletTransaction deposit(Long walletId, DepositRequest req) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet non trouvé : " + walletId));

        // Strategy Pattern : délègue à la stratégie appropriée
        depositStrategyFactory.getStrategy(req.getPaymentMethod())
                .processDeposit(walletId.toString(), req.getAmount());

        double before = wallet.getBalance();
        wallet.setBalance(before + req.getAmount());
        walletRepository.save(wallet);

        WalletTransaction tx = transactionRepository.save(WalletTransaction.builder()
                .wallet(wallet)
                .type(TransactionType.DEPOSIT)
                .amount(req.getAmount())
                .fees(0.0)
                .balanceBefore(before)
                .balanceAfter(wallet.getBalance())
                .description("Dépôt via " + req.getPaymentMethod())
                .reference(UUID.randomUUID().toString())
                .build());

        // Observer Pattern : publie l'événement
        eventPublisher.publishEvent(new WalletEvent(this, "DEPOSIT", wallet.getPhoneNumber(), req.getAmount()));
        return tx;
    }

    @Transactional
    public WalletTransaction withdraw(WithdrawRequest req) {
        Wallet wallet = getWalletByPhone(req.getPhoneNumber());

        double fees = Math.min(req.getAmount() * WITHDRAWAL_FEE_RATE, WITHDRAWAL_FEE_CAP);
        double total = req.getAmount() + fees;

        if (wallet.getBalance() < total) {
            throw new RuntimeException("Solde insuffisant. Besoin : " + total + " | Disponible : " + wallet.getBalance());
        }

        double before = wallet.getBalance();
        wallet.setBalance(before - total);
        walletRepository.save(wallet);

        WalletTransaction tx = transactionRepository.save(WalletTransaction.builder()
                .wallet(wallet)
                .type(TransactionType.WITHDRAW)
                .amount(req.getAmount())
                .fees(fees)
                .balanceBefore(before)
                .balanceAfter(wallet.getBalance())
                .description("Retrait (frais: " + fees + " XOF)")
                .reference(UUID.randomUUID().toString())
                .build());

        eventPublisher.publishEvent(new WalletEvent(this, "WITHDRAW", wallet.getPhoneNumber(), req.getAmount()));
        return tx;
    }

    @Transactional
    public List<WalletTransaction> transfer(TransferRequest req) {
        Wallet sender = getWalletByPhone(req.getSenderPhone());
        Wallet receiver = getWalletByPhone(req.getReceiverPhone());

        if (sender.getBalance() < req.getAmount()) {
            throw new RuntimeException("Solde insuffisant pour le transfert");
        }

        double senderBefore = sender.getBalance();
        double receiverBefore = receiver.getBalance();

        sender.setBalance(senderBefore - req.getAmount());
        receiver.setBalance(receiverBefore + req.getAmount());
        walletRepository.save(sender);
        walletRepository.save(receiver);

        String ref = UUID.randomUUID().toString();
        WalletTransaction sendTx = transactionRepository.save(WalletTransaction.builder()
                .wallet(sender)
                .type(TransactionType.TRANSFER_SEND)
                .amount(req.getAmount())
                .fees(0.0)
                .balanceBefore(senderBefore)
                .balanceAfter(sender.getBalance())
                .description("Transfert vers " + req.getReceiverPhone())
                .reference(ref)
                .build());

        WalletTransaction receiveTx = transactionRepository.save(WalletTransaction.builder()
                .wallet(receiver)
                .type(TransactionType.TRANSFER_RECEIVE)
                .amount(req.getAmount())
                .fees(0.0)
                .balanceBefore(receiverBefore)
                .balanceAfter(receiver.getBalance())
                .description("Transfert reçu de " + req.getSenderPhone())
                .reference(ref)
                .build());

        eventPublisher.publishEvent(new WalletEvent(this, "TRANSFER", sender.getPhoneNumber(), req.getAmount()));
        return List.of(sendTx, receiveTx);
    }

    @Transactional
    public PaymentResponseDto payCurrentMonthBill(PayBillRequest req) {
        Wallet wallet = getWalletByPhone(req.getPhoneNumber());

        // Proxy Pattern : appel via le proxy vers payment-service
        PaymentRequestDto payReq = PaymentRequestDto.builder()
                .walletCode(wallet.getCode())
                .serviceName(req.getServiceName())
                .amount(req.getAmount())
                .build();

        PaymentResponseDto response = paymentServiceProxy.payCurrentMonthBill(payReq);

        if (response.isSuccess() && response.getTotalAmount() != null) {
            deductPayment(wallet, response.getTotalAmount(), req.getServiceName());
        }
        return response;
    }

    @Transactional
    public PaymentResponseDto paySpecificFactures(PayBillRequest req) {
        Wallet wallet = getWalletByPhone(req.getPhoneNumber());

        PaymentRequestDto payReq = PaymentRequestDto.builder()
                .walletCode(wallet.getCode())
                .serviceName(req.getServiceName())
                .factureReferences(req.getFactureReferences())
                .build();

        PaymentResponseDto response = paymentServiceProxy.paySpecificFactures(payReq);

        if (response.isSuccess() && response.getTotalAmount() != null) {
            deductPayment(wallet, response.getTotalAmount(), req.getServiceName());
        }
        return response;
    }

    private void deductPayment(Wallet wallet, Double amount, String serviceName) {
        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Solde insuffisant pour le paiement");
        }
        double before = wallet.getBalance();
        wallet.setBalance(before - amount);
        walletRepository.save(wallet);

        transactionRepository.save(WalletTransaction.builder()
                .wallet(wallet)
                .type(TransactionType.PAYMENT)
                .amount(amount)
                .fees(0.0)
                .balanceBefore(before)
                .balanceAfter(wallet.getBalance())
                .description("Paiement facture " + serviceName)
                .reference(UUID.randomUUID().toString())
                .build());

        eventPublisher.publishEvent(new WalletEvent(this, "PAYMENT", wallet.getPhoneNumber(), amount));
    }

    public List<WalletTransaction> getTransactionHistory(String phone) {
        return transactionRepository.findByWalletPhoneNumberOrderByCreatedAtDesc(phone);
    }

    // Proxy : factures via payment-service
    public List<FactureDto> getFacturesMoisCourant(String walletCode) {
        return paymentServiceProxy.getFacturesMoisCourant(walletCode);
    }

    public List<FactureDto> getFacturesMoisCourantByUnite(String walletCode, String unite) {
        return paymentServiceProxy.getFacturesMoisCourantByUnite(walletCode, unite);
    }

    public List<FactureDto> getFacturesByPeriode(String walletCode, LocalDate debut, LocalDate fin) {
        return paymentServiceProxy.getFacturesByPeriode(walletCode, debut, fin);
    }
}
