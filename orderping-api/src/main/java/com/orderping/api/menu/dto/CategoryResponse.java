package com.orderping.api.menu.dto;

import com.orderping.domain.menu.Category;

public record CategoryResponse(
    Long id,
    String name,
    Boolean isTableFee
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getIsTableFee()
        );
    }
}
