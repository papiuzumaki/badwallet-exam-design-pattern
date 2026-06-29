package com.badwallet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateWalletRequest {
    @NotBlank
    private String phoneNumber;
    private String email;
    private Double initialBalance;
    private String code;
    private String currency;
}
