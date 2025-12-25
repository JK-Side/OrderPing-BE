package com.orderping.api.store.dto;

import com.orderping.domain.store.Store;

import java.time.LocalDateTime;

public record StoreResponse(
        Long id,
        Long userId,
        String name,
        String description,
        Boolean isOpen,
        String imageUrl,
        LocalDateTime createdAt
) {
    public static StoreResponse from(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getUserId(),
                store.getName(),
                store.getDescription(),
                store.getIsOpen(),
                store.getImageUrl(),
                store.getCreatedAt()
        );
    }
}
