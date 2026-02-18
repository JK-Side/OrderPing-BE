package com.orderping.api.menu.dto;

import com.orderping.domain.menu.Menu;

/**
 * 고객용 메뉴 상세 응답. 운영 정보를 노출하지 않도록 필요한 필드만 포함한다.
 */
public record CustomerMenuDetailResponse(
    Long id,
    Long storeId,
    Long categoryId,
    String name,
    Long price,
    String description,
    String imageUrl,
    Boolean isSoldOut
) {

    public static CustomerMenuDetailResponse from(Menu menu) {
        return new CustomerMenuDetailResponse(
            menu.getId(),
            menu.getStoreId(),
            menu.getCategoryId(),
            menu.getName(),
            menu.getPrice(),
            menu.getDescription(),
            menu.getImageUrl(),
            menu.getIsSoldOut()
        );
    }
}

