package com.orderping.api.store.dto;

public record CustomerStoreAccountResponse(
    Long storeId,
    String bankCode,
    String bankName,
    String accountHolder,
    String accountNumber
) {
}
