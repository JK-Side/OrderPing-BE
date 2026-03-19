package com.orderping.api.store.dto;

import jakarta.validation.constraints.Size;

public record StoreUpdateRequest(
    @Size(max = 10, message = "주점명은 10자를 초과할 수 없습니다.")
    String name,
    @Size(max = 100, message = "주점 설명은 100자를 초과할 수 없습니다.")
    String description,
    String imageUrl
) {
}
