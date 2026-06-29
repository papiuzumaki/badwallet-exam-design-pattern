package com.badwallet.controller;

import com.badwallet.dto.*;
import com.badwallet.model.Wallet;
import com.badwallet.model.WalletTransaction;
import com.badwallet.service.WalletService;
import com.badwallet.service.WalletStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final WalletStatsService walletStatsService;

    // 1.1 Seeder la base de données (Async)
    @PostMapping("/seed")
    public ResponseEntity<Map<String, String>> seed(
            @RequestParam(defaultValue = "10") int numWallets,
            @RequestParam(defaultValue = "100") int eventsPerWallet) {
        walletService.seedDatabase(numWallets, eventsPerWallet);
        return ResponseEntity.accepted().body(Map.of("message", "Seeding démarré en arrière-plan"));
    }

    // 1.2 Créer un nouveau portefeuille
    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody CreateWalletRequest request) {
        return ResponseEntity.ok(walletService.createWallet(request));
    }

    // 1.3 Lister tous les portefeuilles (Paginé)
    @GetMapping
    public ResponseEntity<Page<Wallet>> getAllWallets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(walletService.getAllWallets(PageRequest.of(page, size)));
    }

    // 1.4 Consulter un portefeuille par numéro de téléphone
    @GetMapping("/{phone}")
    public ResponseEntity<Wallet> getWallet(@PathVariable String phone) {
        return ResponseEntity.ok(walletService.getWalletByPhone(phone));
    }

    // 1.5 Consulter uniquement le solde à jour
    @GetMapping("/{phone}/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable String phone) {
        Double balance = walletService.getBalance(phone);
        return ResponseEntity.ok(Map.of("phone", phone, "balance", balance, "currency", "XOF"));
    }

    // 1.6 Effectuer un Dépôt
    @PostMapping("/{id}/deposit")
    public ResponseEntity<WalletTransaction> deposit(
            @PathVariable Long id,
            @RequestBody DepositRequest request) {
        return ResponseEntity.ok(walletService.deposit(id, request));
    }

    // 1.7 Effectuer un Retrait
    @PostMapping("/withdraw")
    public ResponseEntity<WalletTransaction> withdraw(@RequestBody WithdrawRequest request) {
        return ResponseEntity.ok(walletService.withdraw(request));
    }

    // 1.8 Effectuer un Transfert
    @PostMapping("/transfer")
    public ResponseEntity<List<WalletTransaction>> transfer(@RequestBody TransferRequest request) {
        return ResponseEntity.ok(walletService.transfer(request));
    }

    // 1.9 Payer une facture du mois en cours via payment-service
    @PostMapping("/pay")
    public ResponseEntity<PaymentResponseDto> payBill(@RequestBody PayBillRequest request) {
        return ResponseEntity.ok(walletService.payCurrentMonthBill(request));
    }

    // 1.10 Payer des factures spécifiques
    @PostMapping("/pay-factures")
    public ResponseEntity<PaymentResponseDto> paySpecificFactures(@RequestBody PayBillRequest request) {
        return ResponseEntity.ok(walletService.paySpecificFactures(request));
    }

    // 1.11 Historique des transactions
    @GetMapping("/{phone}/transactions")
    public ResponseEntity<List<WalletTransaction>> getTransactions(@PathVariable String phone) {
        return ResponseEntity.ok(walletService.getTransactionHistory(phone));
    }

    // 1.12 Statistiques d'un portefeuille
    @GetMapping("/{phone}/stats")
    public ResponseEntity<WalletStatsDto> getStats(@PathVariable String phone) {
        return ResponseEntity.ok(walletStatsService.getStats(phone));
    }
}
