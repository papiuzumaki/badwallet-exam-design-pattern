package com.badwallet.pattern.strategy;

// Strategy Pattern : définit l'interface commune pour toutes les stratégies de dépôt
public interface DepositStrategy {
    void processDeposit(String walletId, Double amount);
    String getMethodName();
}
