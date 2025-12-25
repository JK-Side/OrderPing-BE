package com.orderping.api.menu.dto;

public record MenuCreateRequest(
        Long storeId,
        Long categoryId,
        String name,
        Long price,
        String description,
        String imageUrl,
        Long stock
) {
}
