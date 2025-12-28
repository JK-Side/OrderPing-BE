package com.orderping.api.store.dto;

public record StoreUpdateRequest(
    String name,
    String description,
    String imageUrl
) {
}
