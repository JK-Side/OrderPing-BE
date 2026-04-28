package com.orderping.api.order.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrderCreateRequest(
    @NotNull(message = "테이블 번호는 필수입니다.")
    Integer tableNum,
    @NotNull(message = "주점 ID는 필수입니다.")
    Long storeId,
    @NotNull(message = "입금자명은 필수입니다.")
    String depositorName,
    @Min(value = 0, message = "쿠폰 금액은 0 이상이어야 합니다.")
    Long couponAmount,
    @NotEmpty(message = "메뉴를 1개 이상 선택해야 합니다.")
    @Valid
    List<OrderMenuRequest> menus,
    String idempotencyKey
) {
    public record OrderMenuRequest(
        @NotNull(message = "메뉴 ID는 필수입니다.")
        Long menuId,
        @NotNull(message = "수량은 필수입니다.")
        @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
        Long quantity
    ) {
    }
}
