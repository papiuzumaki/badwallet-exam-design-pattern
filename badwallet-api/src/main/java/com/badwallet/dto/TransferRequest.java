package com.badwallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransferRequest {

    @NotBlank(message = "Le téléphone de l'expéditeur est obligatoire")
    private String senderPhone;

    @NotBlank(message = "Le téléphone du destinataire est obligatoire")
    private String receiverPhone;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double amount;
}
