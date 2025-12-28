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
    private final String sessionId;
    private final String depositorName;
    private final OrderStatus status;
    private final Long totalPrice;
    private final Long couponAmount;
    private final LocalDateTime createdAt;

    public Long getCashAmount() {
        long coupon = couponAmount != null ? couponAmount : 0L;
        long total = totalPrice != null ? totalPrice : 0L;
        return total - coupon;
    }
}
