package com.badwallet.pattern.strategy;

import org.springframework.stereotype.Component;

@Component("WALLET_TARGET")
public class WalletTargetDepositStrategy implements DepositStrategy {

    @Override
    public void processDeposit(String walletId, Double amount) {
        // Traitement spécifique dépôt depuis un autre wallet
        System.out.println("[WALLET_TARGET] Dépôt de " + amount + " vers wallet " + walletId);
    }

    @Override
    public String getMethodName() {
        return "WALLET_TARGET";
    }
}
