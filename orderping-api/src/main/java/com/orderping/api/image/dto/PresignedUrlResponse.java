package com.orderping.api.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Presigned URL 응답")
public record PresignedUrlResponse(
    @Schema(description = "업로드용 Presigned URL (PUT 요청에 사용)")
    String presignedUrl,

    @Schema(description = "업로드 완료 후 이미지 접근 URL (DB 저장용)")
    String imageUrl,

    @Schema(description = "S3 객체 키")
    String key
) {}
