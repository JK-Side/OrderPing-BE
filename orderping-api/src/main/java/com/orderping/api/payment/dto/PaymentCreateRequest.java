package com.orderping.api.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.orderping.domain.enums.PaymentMethod;

public record PaymentCreateRequest(
    @NotNull(message = "주문 ID는 필수입니다.")
    Long orderId,
    @NotNull(message = "결제 수단은 필수입니다.")
    PaymentMethod method,
    @NotNull(message = "결제 금액은 필수입니다.")
    @Min(value = 0, message = "결제 금액은 0 이상이어야 합니다.")
    Long amount
) {
}
