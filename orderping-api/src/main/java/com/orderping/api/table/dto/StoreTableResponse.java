package com.orderping.api.table.dto;

import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.store.StoreTable;

public record StoreTableResponse(
    Long id,
    Long storeId,
    Integer tableNum,
    TableStatus status,
    String qrToken,
    String qrUrl,
    String qrImageUrl
) {
    public static StoreTableResponse from(StoreTable storeTable) {
        return new StoreTableResponse(
            storeTable.getId(),
            storeTable.getStoreId(),
            storeTable.getTableNum(),
            storeTable.getStatus(),
            null,
            null,
            storeTable.getQrImageUrl()
        );
    }

    public static StoreTableResponse from(StoreTable storeTable, String qrToken, String qrUrl) {
        return new StoreTableResponse(
            storeTable.getId(),
            storeTable.getStoreId(),
            storeTable.getTableNum(),
            storeTable.getStatus(),
            qrToken,
            qrUrl,
            storeTable.getQrImageUrl()
        );
    }
}
