package com.orderping.api.statistics.dto;

import java.util.List;

public record MenuStatisticsResponse(
    List<MenuStat> menus
) {
    public record MenuStat(
        Long menuId,
        String menuName,
        Long stock,
        Long soldQuantity
    ) {}
}
