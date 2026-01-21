package com.orderping.api.table.dto;

public record OrderMenuSummary(
    Long menuId,
    String menuName,
    Long quantity,
    Long price
) {
}
