package com.orderping.api.order.dto;

import java.util.List;

public record OrderCreateRequest(
    Long tableId,
    Long storeId,
    String sessionId,
    String depositorName,
    Long couponAmount,
    List<OrderMenuRequest> menus
) {
    public record OrderMenuRequest(
        Long menuId,
        Long quantity,
        Long price,
        Boolean isService
    ) {
    }
}
