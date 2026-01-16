package com.orderping.api.qr.dto;

import java.util.List;

import com.orderping.api.store.dto.StoreDetailResponse.CategoryWithMenusResponse;

public record TableQrInfoResponse(
    Long storeId,
    Long tableId,
    Integer tableNum,
    String storeName,
    String storeDescription,
    String storeImageUrl,
    Boolean isOpen,
    List<CategoryWithMenusResponse> categories,
    AccountInfo account
) {
    public record AccountInfo(
        String bankCode,
        String bankName,
        String accountHolder,
        String accountNumberMask
    ) {
    }
}
