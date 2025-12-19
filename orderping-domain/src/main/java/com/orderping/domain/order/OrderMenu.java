package com.orderping.domain.order;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderMenu {
    private final Long id;
    private final Long orderId;
    private final Long menuId;
    private final Long quantity;
    private final Long price;
}
