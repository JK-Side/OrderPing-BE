package com.orderping.api.store.dto;

import jakarta.validation.constraints.Size;

public record StoreCreateRequest(
    @Size(max = 50, message = "주점명은 50자를 초과할 수 없습니다.")
    String name,
    String description,
    String imageUrl,
    String bankCode,
    String accountHolder,
    String accountNumber
) {
}
