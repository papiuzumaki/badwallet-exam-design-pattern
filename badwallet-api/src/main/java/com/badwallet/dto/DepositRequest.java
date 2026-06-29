package com.badwallet.dto;

import lombok.Data;

@Data
public class DepositRequest {
    private Double amount;
    private String paymentMethod; // CREDIT_CARD, WALLET_TARGET
}
