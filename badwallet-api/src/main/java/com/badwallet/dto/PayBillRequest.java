package com.badwallet.dto;

import lombok.Data;
import java.util.List;

@Data
public class PayBillRequest {
    private String phoneNumber;
    private String serviceName;
    private Double amount;
    private List<String> factureReferences;
}
