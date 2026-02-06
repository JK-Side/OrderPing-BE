package com.orderping.api.table.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "테이블 단체 삭제 요청")
public record StoreTableBulkDeleteRequest(
    @Schema(description = "매장 ID", example = "1")
    @NotNull(message = "매장 ID는 필수입니다.")
    Long storeId,

    @Schema(description = "삭제할 테이블 번호 목록", example = "[1, 2, 3]")
    @NotEmpty(message = "삭제할 테이블 번호는 최소 1개 이상 필요합니다.")
    List<Integer> tableNums
) {
}
