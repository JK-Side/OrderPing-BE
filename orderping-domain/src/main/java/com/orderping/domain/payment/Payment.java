package com.orderping.domain.payment;

import com.orderping.domain.enums.PaymentMethod;
import com.orderping.domain.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
