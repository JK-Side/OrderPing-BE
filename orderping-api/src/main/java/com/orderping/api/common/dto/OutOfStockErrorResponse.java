package com.orderping.api.common.dto;

import java.util.List;

public record OutOfStockErrorResponse(
    String code,
    String message,
    List<StockItemDto> items
) {
    public record StockItemDto(
        Long menuId,
        String menuName,
        Long requestedQuantity,
        long availableStock
    ) {
    }

    public static OutOfStockErrorResponse of(String message, List<StockItemDto> items) {
        return new OutOfStockErrorResponse("INSUFFICIENT_STOCK", message, items);
    }
}
