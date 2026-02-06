package com.orderping.api.table.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "테이블 단체 QR 업데이트 요청")
public record StoreTableBulkQrUpdateRequest(
    @Schema(description = "테이블별 QR 정보 목록")
    @NotEmpty(message = "QR 정보 목록은 필수입니다.")
    @Valid
    List<TableQrUpdate> updates
) {
    @Schema(description = "테이블 QR 업데이트 정보")
    public record TableQrUpdate(
        @Schema(description = "테이블 ID", example = "1")
        @NotNull(message = "테이블 ID는 필수입니다.")
        Long tableId,

        @Schema(description = "QR 이미지 URL", example = "https://s3.../qr1.png")
        @NotNull(message = "QR 이미지 URL은 필수입니다.")
        String qrImageUrl
    ) {
    }
}
