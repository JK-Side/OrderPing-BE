package com.orderping.api.menu.dto;

import com.orderping.domain.menu.Menu;

public record MenuResponse(
        Long id,
        Long storeId,
        Long categoryId,
        String name,
        Long price,
        String description,
        String imageUrl,
        Long initialStock,
        Long stock,
        Long soldCount,
        Boolean isSoldOut
) {
    public static MenuResponse from(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getStoreId(),
                menu.getCategoryId(),
                menu.getName(),
                menu.getPrice(),
                menu.getDescription(),
                menu.getImageUrl(),
                menu.getInitialStock(),
                menu.getStock(),
                menu.getSoldCount(),
                menu.getIsSoldOut()
        );
    }
}
