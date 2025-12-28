package com.orderping.domain.bank;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Bank {
    private final String code;
    private final String name;
    private final Boolean isActive;
}
