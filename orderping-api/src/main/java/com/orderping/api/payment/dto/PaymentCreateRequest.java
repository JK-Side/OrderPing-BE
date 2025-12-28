package com.orderping.api.payment.dto;

import com.orderping.domain.enums.PaymentMethod;

public record PaymentCreateRequest(
    Long orderId,
    PaymentMethod method,
    Long amount
) {
}
