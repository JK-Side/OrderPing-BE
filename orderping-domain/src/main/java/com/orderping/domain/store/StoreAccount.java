package com.orderping.domain.store;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreAccount {
    private final Long id;
    private final Long storeId;
    private final String bankCode;
    private final String accountNumberEnc;
    private final String accountNumberMask;
    private final Boolean isActive;
}
