package com.orderping.api.table.dto;

public record StoreTableCreateRequest(
    Long storeId,
    Integer tableNum
) {
}
