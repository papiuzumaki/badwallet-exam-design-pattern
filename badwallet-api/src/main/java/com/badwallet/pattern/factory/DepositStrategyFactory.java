package com.badwallet.pattern.factory;

import com.badwallet.pattern.strategy.DepositStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

// Factory Pattern : sélectionne et retourne la stratégie de dépôt appropriée
@Component
public class DepositStrategyFactory {

    private final Map<String, DepositStrategy> strategies;

    public DepositStrategyFactory(Map<String, DepositStrategy> strategies) {
        this.strategies = strategies;
    }

    public DepositStrategy getStrategy(String paymentMethod) {
        DepositStrategy strategy = strategies.get(paymentMethod);
        if (strategy == null) {
            throw new IllegalArgumentException("Méthode de paiement inconnue : " + paymentMethod);
        }
        return strategy;
    }
}
