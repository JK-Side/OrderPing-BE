package com.orderping.api.order.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.orderping.domain.enums.OrderStatus;
import com.orderping.domain.order.Order;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 상세 응답 (메뉴 포함)")
public record OrderDetailResponse(
    @Schema(description = "주문 ID")
    Long id,

    @Schema(description = "테이블 ID")
    Long tableId,

    @Schema(description = "테이블 번호")
    Integer tableNum,

    @Schema(description = "매장 ID")
    Long storeId,

    @Schema(description = "입금자명")
    String depositorName,

    @Schema(description = "주문 상태")
    OrderStatus status,

    @Schema(description = "총 금액")
    Long totalPrice,

    @Schema(description = "쿠폰 할인 금액")
    Long couponAmount,

    @Schema(description = "실제 결제 금액")
    Long cashAmount,

    @Schema(description = "주문 생성 시간")
    LocalDateTime createdAt,

    @Schema(description = "주문 메뉴 목록")
    List<OrderMenuDetail> menus
) {
    public static OrderDetailResponse from(Order order, List<OrderMenuDetail> menus) {
        return new OrderDetailResponse(
            order.getId(),
            order.getTableId(),
            order.getTableNum(),
            order.getStoreId(),
            order.getDepositorName(),
            order.getStatus(),
            order.getTotalPrice(),
            order.getCouponAmount(),
            order.getCashAmount(),
            order.getCreatedAt(),
            menus
        );
    }

    @Schema(description = "주문 메뉴 상세")
    public record OrderMenuDetail(
        @Schema(description = "메뉴 ID")
        Long menuId,

        @Schema(description = "메뉴 이름")
        String menuName,

        @Schema(description = "수량")
        Long quantity,

        @Schema(description = "가격")
        Long price,

        @Schema(description = "서비스 여부")
        Boolean isService
    ) {
    }
}
