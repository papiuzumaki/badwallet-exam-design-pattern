package com.badwallet.pattern.observer;

import org.springframework.context.ApplicationEvent;

// Observer Pattern : événement publié lors d'opérations sur le wallet
public class WalletEvent extends ApplicationEvent {

    private final String eventType;
    private final String phoneNumber;
    private final Double amount;

    public WalletEvent(Object source, String eventType, String phoneNumber, Double amount) {
        super(source);
        this.eventType = eventType;
        this.phoneNumber = phoneNumber;
        this.amount = amount;
    }

    public String getEventType() { return eventType; }
    public String getPhoneNumber() { return phoneNumber; }
    public Double getAmount() { return amount; }
}
