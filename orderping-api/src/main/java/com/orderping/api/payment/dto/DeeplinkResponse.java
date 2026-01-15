package com.orderping.api.payment.dto;

public record DeeplinkResponse(
    Long amount,
    String tossDeeplink,
    AccountInfo account
) {
    public record AccountInfo(
        String bankCode,
        String bankName,
        String accountHolder,
        String accountNumber
    ) {}
}
