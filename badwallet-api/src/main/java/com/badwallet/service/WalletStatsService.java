package com.badwallet.service;

import com.badwallet.dto.WalletStatsDto;
import com.badwallet.model.TransactionType;
import com.badwallet.model.Wallet;
import com.badwallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletStatsService {

    private final WalletService walletService;
    private final WalletTransactionRepository transactionRepository;

    public WalletStatsDto getStats(String phone) {
        Wallet wallet = walletService.getWalletByPhone(phone);

        return WalletStatsDto.builder()
                .phoneNumber(wallet.getPhoneNumber())
                .code(wallet.getCode())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .totalTransactions(transactionRepository.countByWalletPhoneNumber(phone))
                .totalDepose(transactionRepository.sumAmountByPhoneAndType(phone, TransactionType.DEPOSIT))
                .totalRetire(transactionRepository.sumAmountByPhoneAndType(phone, TransactionType.WITHDRAW))
                .totalTransfere(transactionRepository.sumAmountByPhoneAndType(phone, TransactionType.TRANSFER_SEND))
                .totalPaye(transactionRepository.sumAmountByPhoneAndType(phone, TransactionType.PAYMENT))
                .build();
    }
}
