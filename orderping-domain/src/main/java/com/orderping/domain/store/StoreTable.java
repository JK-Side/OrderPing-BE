package com.orderping.domain.store;

import com.orderping.domain.enums.TableStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreTable {
    private final Long id;
    private final Long storeId;
    private final Integer tableNum;
    private final TableStatus status;
}
