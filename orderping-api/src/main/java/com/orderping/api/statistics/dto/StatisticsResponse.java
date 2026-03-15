package com.orderping.api.statistics.dto;

import java.time.LocalDateTime;
import java.util.List;

public record StatisticsResponse(
    long totalRevenue,
    long transferRevenue,
    long couponRevenue,
    int orderCount,
    List<OrderSummary> orders
) {
    public record OrderSummary(
        Long orderId,
        Integer tableNum,
        LocalDateTime orderedAt,
        List<MenuDetail> menus,
        Long totalPrice,
        String depositorName
    ) {}

    public record MenuDetail(
        String menuName,
        Long quantity,
        Long price,
        Boolean isService
    ) {}
}
