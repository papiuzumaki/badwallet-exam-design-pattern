package com.badwallet.dto;

import lombok.Data;
import java.util.List;

@Data
public class PaymentResponseDto {
    private boolean success;
    private String message;
    private List<String> paidReferences;
    private Double totalAmount;
}
