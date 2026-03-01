package com.orderping.api.order.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.orderping.domain.enums.OrderStatus;
import com.orderping.domain.order.Order;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "고객용 주문 상세 응답 (메뉴 포함, 테이블 내 주문 순서 포함)")
public record CustomerOrderDetailResponse(
    @Schema(description = "주문 ID")
    Long id,

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
    List<OrderDetailResponse.OrderMenuDetail> menus,

    @Schema(description = "테이블 내 주문 순서 (1부터 시작)")
    int orderIndex
) {
    public static CustomerOrderDetailResponse from(Order order, List<OrderDetailResponse.OrderMenuDetail> menus, int orderIndex) {
        return new CustomerOrderDetailResponse(
            order.getId(),
            order.getTableNum(),
            order.getStoreId(),
            order.getDepositorName(),
            order.getStatus(),
            order.getTotalPrice(),
            order.getCouponAmount(),
            order.getCashAmount(),
            order.getCreatedAt(),
            menus,
            orderIndex
        );
    }
}
