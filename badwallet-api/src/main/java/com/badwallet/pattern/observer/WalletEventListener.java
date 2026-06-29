package com.badwallet.pattern.observer;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

// Observer Pattern : écoute et traite les événements wallet de façon asynchrone
@Component
public class WalletEventListener {

    @Async
    @EventListener
    public void onWalletEvent(WalletEvent event) {
        System.out.printf("[EVENT] %s | Phone: %s | Amount: %.2f%n",
                event.getEventType(), event.getPhoneNumber(), event.getAmount());
        // Ici on pourrait envoyer SMS, email, notification push...
    }
}
