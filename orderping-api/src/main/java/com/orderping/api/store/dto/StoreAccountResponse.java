package com.orderping.api.store.dto;

import com.orderping.domain.store.StoreAccount;

public record StoreAccountResponse(
        Long id,
        Long storeId,
        String bankCode,
        String accountHolder,
        String accountNumberMask,
        Boolean isActive
) {
    public static StoreAccountResponse from(StoreAccount storeAccount) {
        return new StoreAccountResponse(
                storeAccount.getId(),
                storeAccount.getStoreId(),
                storeAccount.getBankCode(),
                storeAccount.getAccountHolder(),
                storeAccount.getAccountNumberMask(),
                storeAccount.getIsActive()
        );
    }
}
