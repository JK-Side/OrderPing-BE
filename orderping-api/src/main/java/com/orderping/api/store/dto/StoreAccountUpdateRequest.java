package com.orderping.api.store.dto;

public record StoreAccountUpdateRequest(
        String bankCode,
        String accountHolder,
        String accountNumber
) {
}
