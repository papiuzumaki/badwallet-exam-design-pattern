package com.badwallet.repository;

import com.badwallet.model.TransactionType;
import com.badwallet.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByWalletPhoneNumberOrderByCreatedAtDesc(String phoneNumber);

    long countByWalletPhoneNumber(String phoneNumber);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM WalletTransaction t WHERE t.wallet.phoneNumber = :phone AND t.type = :type")
    Double sumAmountByPhoneAndType(@Param("phone") String phone, @Param("type") TransactionType type);
}
