package com.orderping.domain.store;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

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
