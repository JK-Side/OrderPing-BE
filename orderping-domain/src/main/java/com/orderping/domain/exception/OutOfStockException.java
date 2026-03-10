package com.orderping.domain.exception;

public class OutOfStockException extends RuntimeException {

    private final long currentStock;

    public OutOfStockException(String message, long currentStock) {
        super(message);
        this.currentStock = currentStock;
    }

    public long getCurrentStock() {
        return currentStock;
    }
}
