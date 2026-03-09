package com.orderping.api.user.dto;

import java.util.List;

public record MyPageResponse(
    Long userId,
    List<StoreInfo> stores
) {
    public record StoreInfo(
        Long storeId,
        String name,
        String description,
        String imageUrl,
        AccountInfo account
    ) {}

    public record AccountInfo(
        String bankCode,
        String bankName,
        String accountHolder,
        String accountNumber
    ) {
        public static AccountInfo empty() {
            return new AccountInfo("", "계좌 미등록", "", "");
        }
    }
}
