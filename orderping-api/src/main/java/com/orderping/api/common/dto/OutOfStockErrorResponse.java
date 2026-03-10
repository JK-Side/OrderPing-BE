package com.orderping.api.common.dto;

import java.time.LocalDateTime;

public record OutOfStockErrorResponse(
    int status,
    String code,
    String message,
    long currentStock,
    LocalDateTime timestamp
) {
    public static OutOfStockErrorResponse of(int status, String code, String message, long currentStock) {
        return new OutOfStockErrorResponse(status, code, message, currentStock, LocalDateTime.now());
    }
}
