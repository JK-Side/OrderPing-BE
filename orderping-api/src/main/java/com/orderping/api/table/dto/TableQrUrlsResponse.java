package com.orderping.api.table.dto;

import java.util.List;

public record TableQrUrlsResponse(
    Long storeId,
    List<TableQrUrlResponse> tables
) {
}
