package com.orderping.api.menu.dto;

import jakarta.validation.constraints.Size;

public record MenuUpdateRequest(
    Long categoryId,
    @Size(max = 20, message = "메뉴명은 20자를 초과할 수 없습니다.")
    String name,
    Long price,
    @Size(max = 30, message = "메뉴 설명은 30자를 초과할 수 없습니다.")
    String description,
    String imageUrl,
    Long initialStock,
    Long stock,
    Boolean isSoldOut
) {
}
