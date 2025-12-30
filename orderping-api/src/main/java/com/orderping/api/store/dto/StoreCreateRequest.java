package com.orderping.api.store.dto;

public record StoreCreateRequest(
    String name,
    String description,
    String imageUrl,
    String bankCode,
    String accountHolder,
    String accountNumber
) {
}
