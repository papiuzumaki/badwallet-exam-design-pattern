package com.badwallet.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PaymentRequestDto {
    private String walletCode;
    private String serviceName;
    private Double amount;
    private List<String> factureReferences;
}
