package com.orderping.api.order.dto;

import com.orderping.domain.enums.OrderStatus;

public record OrderStatusUpdateRequest(
    OrderStatus status
) {
}
