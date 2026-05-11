package com.orderping.domain.exception;

import java.util.List;

public class OutOfStockException extends RuntimeException {

    private final List<StockItem> items;

    public OutOfStockException(String message, List<StockItem> items) {
        super(message);
        this.items = items;
    }

    public List<StockItem> getItems() {
        return items;
    }

    public record StockItem(
        Long menuId,
        String menuName,
        Long requestedQuantity,
        long availableStock
    ) {
    }
}
