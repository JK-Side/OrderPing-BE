package com.orderping.api.table.dto;

public record StoreTableBulkCreateRequest(
    Long storeId,
    Integer count
) {
}
