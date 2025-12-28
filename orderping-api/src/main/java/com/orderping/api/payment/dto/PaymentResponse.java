package com.orderping.api.payment.dto;

import java.time.LocalDateTime;

import com.orderping.domain.enums.PaymentMethod;
import com.orderping.domain.enums.PaymentStatus;
import com.orderping.domain.payment.Payment;

public record PaymentResponse(
    Long id,
    Long orderId,
    PaymentMethod method,
    Long amount,
    PaymentStatus status,
    LocalDateTime createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getOrderId(),
            payment.getMethod(),
            payment.getAmount(),
            payment.getStatus(),
            payment.getCreatedAt()
        );
    }
}
