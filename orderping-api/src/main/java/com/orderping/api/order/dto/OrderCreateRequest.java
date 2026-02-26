package com.orderping.api.order.dto;

import java.util.List;

public record OrderCreateRequest(
    Integer tableNum,
    Long storeId,
    String depositorName,
    Long couponAmount,
    List<OrderMenuRequest> menus
) {
    public record OrderMenuRequest(
        Long menuId,
        Long quantity
    ) {
    }
}
