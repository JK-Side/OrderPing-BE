package com.orderping.api.order.dto;

import java.util.List;

public record ServiceOrderCreateRequest(
    Integer tableNum,
    Long storeId,
    List<ServiceMenuRequest> menus
) {
    public record ServiceMenuRequest(
        Long menuId,
        Long quantity
    ) {
    }
}
