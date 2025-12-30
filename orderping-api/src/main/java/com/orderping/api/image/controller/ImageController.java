package com.orderping.api.image.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderping.api.image.dto.PresignedUrlRequest;
import com.orderping.api.image.dto.PresignedUrlResponse;
import com.orderping.external.s3.S3Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController implements ImageApi {

    private final S3Service s3Service;

    @PostMapping("/presigned-url")
    @Override
    public ResponseEntity<PresignedUrlResponse> generatePresignedUrl(@RequestBody PresignedUrlRequest request) {
        S3Service.PresignedUrlResponse s3Response = s3Service.generatePresignedUrl(
            request.directory(),
            request.fileName()
        );

        PresignedUrlResponse response = new PresignedUrlResponse(
            s3Response.presignedUrl(),
            s3Response.imageUrl(),
            s3Response.key(),
            s3Response.maxFileSize()
        );

        return ResponseEntity.ok(response);
    }
}
