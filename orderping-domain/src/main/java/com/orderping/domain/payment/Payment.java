package com.orderping.domain.payment;

import java.time.LocalDateTime;

import com.orderping.domain.enums.PaymentMethod;
import com.orderping.domain.enums.PaymentStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Payment {
    private final Long id;
    private final Long orderId;
    private final PaymentMethod method;
    private final Long amount;
    private final PaymentStatus status;
    private final LocalDateTime createdAt;
}
