package com.orderping.api.image.controller;

import org.springframework.http.ResponseEntity;

import com.orderping.api.image.dto.PresignedUrlRequest;
import com.orderping.api.image.dto.PresignedUrlResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Image", description = "이미지 업로드 API")
public interface ImageApi {

    @Operation(
        summary = "Presigned URL 생성",
        description = "S3 이미지 업로드용 Presigned URL을 생성합니다. " +
            "반환된 presignedUrl로 PUT 요청하여 이미지를 업로드하고, " +
            "imageUrl을 DB에 저장하세요."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Presigned URL 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<PresignedUrlResponse> generatePresignedUrl(PresignedUrlRequest request);
}
