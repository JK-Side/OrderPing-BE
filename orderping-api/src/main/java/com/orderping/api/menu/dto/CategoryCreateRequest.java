package com.orderping.api.menu.dto;

public record CategoryCreateRequest(
    String name,
    Boolean isTableFee
) {
}
