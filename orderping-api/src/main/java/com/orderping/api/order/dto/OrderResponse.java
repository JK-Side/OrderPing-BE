package com.orderping.api.order.dto;

import java.time.LocalDateTime;

import com.orderping.domain.enums.OrderStatus;
import com.orderping.domain.order.Order;

public record OrderResponse(
    Long id,
    Long tableId,
    Long storeId,
    String sessionId,
    String depositorName,
    OrderStatus status,
    Long totalPrice,
    Long couponAmount,
    Long cashAmount,
    LocalDateTime createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getTableId(),
            order.getStoreId(),
            order.getSessionId(),
            order.getDepositorName(),
            order.getStatus(),
            order.getTotalPrice(),
            order.getCouponAmount(),
            order.getCashAmount(),
            order.getCreatedAt()
        );
    }
}
