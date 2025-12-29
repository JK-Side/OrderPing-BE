package com.orderping.external.s3;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofMinutes(10);

    /**
     * 이미지 업로드용 Presigned URL 생성
     *
     * @param directory 저장 디렉토리 (예: "menus", "stores")
     * @param originalFileName 원본 파일명
     * @return Presigned URL 정보
     */
    public PresignedUrlResponse generatePresignedUrl(String directory, String originalFileName) {
        String extension = extractExtension(originalFileName);
        String key = generateKey(directory, extension);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(s3Properties.getBucket())
            .key(key)
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(PRESIGNED_URL_EXPIRATION)
            .putObjectRequest(objectRequest)
            .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
            s3Properties.getBucket(),
            s3Properties.getRegion(),
            key);

        return new PresignedUrlResponse(
            presignedRequest.url().toString(),
            imageUrl,
            key
        );
    }

    private String generateKey(String directory, String extension) {
        String uuid = UUID.randomUUID().toString();
        return String.format("%s/%s%s", directory, uuid, extension);
    }

    private String extractExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public record PresignedUrlResponse(
        String presignedUrl,
        String imageUrl,
        String key
    ) {}
}
