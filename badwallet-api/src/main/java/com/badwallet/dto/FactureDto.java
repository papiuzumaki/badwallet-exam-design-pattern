package com.badwallet.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FactureDto {
    private Long id;
    private String reference;
    private String walletCode;
    private String serviceName;
    private String unite;
    private Double montant;
    private Boolean payee;
    private LocalDate dateFacture;
    private String mois;
}
