package com.orderping.api.table.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "테이블 메모 수정 요청")
public record StoreTableMemoUpdateRequest(
    @Schema(description = "메모 내용 (최대 100자, 빈 문자열로 초기화 가능)", example = "손님 요청: 얼음 빼주세요")
    @Size(max = 100, message = "메모는 100자를 초과할 수 없습니다.")
    String memo
) {
    public String memoOrEmpty() {
        return memo != null ? memo : "";
    }
}
