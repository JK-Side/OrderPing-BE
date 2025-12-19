package com.orderping.domain.menu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Category {
    private final Long id;
    private final Long storeId;
    private final String name;
}
