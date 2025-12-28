package com.orderping.api.store.dto;

public record StoreAccountCreateRequest(
    Long storeId,
    String bankCode,
    String accountHolder,
    String accountNumber
) {
}
