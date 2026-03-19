package com.orderping.api.store.dto;

import jakarta.validation.constraints.Size;

public record StoreCreateRequest(
    @Size(max = 10, message = "주점명은 10자를 초과할 수 없습니다.")
    String name,
    @Size(max = 100, message = "주점 설명은 100자를 초과할 수 없습니다.")
    String description,
    String imageUrl,
    String bankCode,
    @Size(max = 6, message = "예금주명은 6자를 초과할 수 없습니다.")
    String accountHolder,
    @Size(max = 20, message = "계좌번호는 20자를 초과할 수 없습니다.")
    String accountNumber
) {
}
