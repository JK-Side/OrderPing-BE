package com.orderping.api.main.dto;

import java.util.List;

public record MainResponse(
        String userName,
        List<StoreSimpleResponse> stores
) {
    public record StoreSimpleResponse(
            Long id,
            String name,
            String imageUrl
    ) {
    }
}
