package com.orderping.api.table.dto;

import java.util.List;

import com.orderping.domain.enums.OrderStatus;
import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.store.StoreTable;

public record StoreTableDetailResponse(
    Long id,
    Long storeId,
    Integer tableNum,
    TableStatus status,
    String qrImageUrl,
    List<OrderMenuSummary> orderMenus,
    List<OrderMenuSummary> serviceMenus,
    Long totalOrderAmount,
    OrderStatus orderStatus
) {
    public static StoreTableDetailResponse from(
        StoreTable storeTable,
        List<OrderMenuSummary> orderMenus,
        List<OrderMenuSummary> serviceMenus,
        Long totalOrderAmount,
        OrderStatus orderStatus
    ) {
        return new StoreTableDetailResponse(
            storeTable.getId(),
            storeTable.getStoreId(),
            storeTable.getTableNum(),
            storeTable.getStatus(),
            storeTable.getQrImageUrl(),
            orderMenus,
            serviceMenus,
            totalOrderAmount,
            orderStatus
        );
    }
}
