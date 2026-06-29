package com.badwallet.dto;

import lombok.Data;

@Data
public class WithdrawRequest {
    private String phoneNumber;
    private Double amount;
}
