package com.orderping.domain.order;

import com.orderping.domain.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Order {
    private final Long id;
    private final Long tableId;
    private final Long storeId;
    private final OrderStatus status;
    private final Long totalPrice;
    private final LocalDateTime createdAt;
}
