package com.orderping.api.user.dto;

import java.util.List;

public record MyPageResponse(
    String email,
    List<StoreInfo> stores
) {
    public record StoreInfo(
        Long storeId,
        String name,
        String description,
        AccountInfo account
    ) {}

    public record AccountInfo(
        String bankCode,
        String bankName,
        String accountHolder,
        String accountNumber
    ) {}
}
