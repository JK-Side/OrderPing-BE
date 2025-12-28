package com.orderping.domain.menu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Menu {
    private final Long id;
    private final Long storeId;
    private final Long categoryId;
    private final String name;
    private final Long price;
    private final String description;
    private final String imageUrl;
    private final Long initialStock;
    private final Long stock;
    private final Boolean isSoldOut;

    public Long getSoldCount() {
        long initial = initialStock != null ? initialStock : 0L;
        long current = stock != null ? stock : 0L;
        return initial - current;
    }
}
