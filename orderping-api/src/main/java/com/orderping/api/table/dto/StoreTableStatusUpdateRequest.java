package com.orderping.api.table.dto;

import com.orderping.domain.enums.TableStatus;

public record StoreTableStatusUpdateRequest(
    TableStatus status
) {
}
