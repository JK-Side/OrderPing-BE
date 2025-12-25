package com.orderping.api.store.dto;

public record StoreCreateRequest(
        Long userId,
        String name,
        String description,
        String imageUrl,
        String bankCode,
        String accountHolder,
        String accountNumber
) {
}
