package com.badwallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DepositRequest {

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double amount;

    @NotBlank(message = "La méthode de paiement est obligatoire (CREDIT_CARD ou WALLET_TARGET)")
    private String paymentMethod;
}
