package com.orderping.api.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Presigned URL 요청")
public record PresignedUrlRequest(
    @Schema(description = "저장 디렉토리", example = "menus", allowableValues = {"menus", "stores"})
    String directory,

    @Schema(description = "원본 파일명", example = "chicken.jpg")
    String fileName
) {
}
