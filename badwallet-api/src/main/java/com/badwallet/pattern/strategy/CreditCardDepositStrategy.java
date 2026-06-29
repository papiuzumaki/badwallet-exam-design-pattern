package com.badwallet.pattern.strategy;

import org.springframework.stereotype.Component;

@Component("CREDIT_CARD")
public class CreditCardDepositStrategy implements DepositStrategy {

    @Override
    public void processDeposit(String walletId, Double amount) {
        // Traitement spécifique carte bancaire : vérification, autorisation
        System.out.println("[CREDIT_CARD] Traitement dépôt de " + amount + " pour wallet " + walletId);
    }

    @Override
    public String getMethodName() {
        return "CREDIT_CARD";
    }
}
