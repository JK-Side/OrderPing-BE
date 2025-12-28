package com.orderping.api.menu.dto;

public record MenuUpdateRequest(
    Long categoryId,
    String name,
    Long price,
    String description,
    String imageUrl,
    Long initialStock,
    Long stock,
    Boolean isSoldOut
) {
}
