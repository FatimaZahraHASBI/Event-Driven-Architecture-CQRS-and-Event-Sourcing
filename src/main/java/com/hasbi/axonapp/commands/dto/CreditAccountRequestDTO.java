package com.hasbi.axonapp.commands.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditAccountRequestDTO {
    private String accountId;
    private BigDecimal amount;
    private String currency;
}
