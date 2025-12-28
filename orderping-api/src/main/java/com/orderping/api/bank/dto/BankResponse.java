package com.orderping.api.bank.dto;

import com.orderping.domain.bank.Bank;

public record BankResponse(
    String code,
    String name
) {
    public static BankResponse from(Bank bank) {
        return new BankResponse(bank.getCode(), bank.getName());
    }
}
