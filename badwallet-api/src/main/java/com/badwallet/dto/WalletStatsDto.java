package com.badwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletStatsDto {
    private String phoneNumber;
    private String code;
    private Double balance;
    private String currency;
    private Long totalTransactions;
    private Double totalDepose;
    private Double totalRetire;
    private Double totalTransfere;
    private Double totalPaye;
}
