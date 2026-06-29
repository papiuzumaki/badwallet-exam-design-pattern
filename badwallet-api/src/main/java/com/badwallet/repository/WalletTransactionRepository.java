package com.badwallet.repository;

import com.badwallet.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWalletPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
}
