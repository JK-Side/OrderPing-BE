package com.orderping.api.table.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "테이블 생성 요청")
public record StoreTableCreateRequest(
    @Schema(description = "매장 ID", example = "1")
    Long storeId,

    @Schema(description = "테이블 번호", example = "1")
    Integer tableNum,

    @Schema(description = "QR 이미지 URL (선택)", example = "https://s3.../qr.png")
    String qrImageUrl
) {
}
