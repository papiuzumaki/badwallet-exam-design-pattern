package com.badwallet.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private String senderPhone;
    private String receiverPhone;
    private Double amount;
}
