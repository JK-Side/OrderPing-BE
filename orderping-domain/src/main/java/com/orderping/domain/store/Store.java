package com.orderping.domain.store;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Store {
    private final Long id;
    private final Long userId;
    private final String name;
    private final String description;
    private final LocalDateTime createdAt;
    private final Boolean isOpen;
    private final String imageUrl;
}
