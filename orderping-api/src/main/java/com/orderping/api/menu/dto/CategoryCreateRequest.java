package com.orderping.api.menu.dto;

public record CategoryCreateRequest(
        Long storeId,
        String name
) {
}
