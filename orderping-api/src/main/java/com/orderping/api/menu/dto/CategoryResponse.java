package com.orderping.api.menu.dto;

import com.orderping.domain.menu.Category;

public record CategoryResponse(
        Long id,
        Long storeId,
        String name
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getStoreId(),
                category.getName()
        );
    }
}
