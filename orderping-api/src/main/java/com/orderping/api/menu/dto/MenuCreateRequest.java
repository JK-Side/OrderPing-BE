package com.orderping.api.menu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MenuCreateRequest(
    @NotNull(message = "storeId는 필수입니다.")
    Long storeId,
    @NotNull(message = "categoryId는 필수입니다.")
    Long categoryId,
    @NotBlank(message = "메뉴명은 필수입니다.")
    @Size(max = 20, message = "메뉴명은 20자를 초과할 수 없습니다.")
    String name,
    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    Long price,
    @Size(max = 30, message = "메뉴 설명은 30자를 초과할 수 없습니다.")
    String description,
    String imageUrl,
    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    Long stock,
    Boolean isTableFee
) {
}
