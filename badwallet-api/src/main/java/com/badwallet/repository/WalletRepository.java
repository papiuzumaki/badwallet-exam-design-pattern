package com.badwallet.repository;

import com.badwallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByPhoneNumber(String phoneNumber);
    Optional<Wallet> findByCode(String code);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByCode(String code);
    Page<Wallet> findAll(Pageable pageable);
}
