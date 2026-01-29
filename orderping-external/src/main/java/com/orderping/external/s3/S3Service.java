package com.orderping.external.s3;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.orderping.domain.exception.BadRequestException;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofMinutes(10);
    private static final Set<String> ALLOWED_DIRECTORIES = Set.of("menus", "stores", "tables");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp", ".svg");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    /**
     * 이미지 업로드용 Presigned URL 생성
     *
     * @param directory        저장 디렉토리 (예: "menus", "stores")
     * @param originalFileName 원본 파일명
     * @return Presigned URL 정보
     */
    public PresignedUrlResponse generatePresignedUrl(String directory, String originalFileName) {
        validateDirectory(directory);
        validateFileName(originalFileName);

        String extension = extractExtension(originalFileName).toLowerCase();
        validateExtension(extension);

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
            key,
            MAX_FILE_SIZE
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

    private void validateDirectory(String directory) {
        if (directory == null || directory.isBlank()) {
            throw new BadRequestException("디렉토리명은 필수입니다.");
        }
        if (directory.contains("..") || directory.contains("/") || directory.contains("\\")) {
            throw new BadRequestException("유효하지 않은 디렉토리명입니다.");
        }
        if (!ALLOWED_DIRECTORIES.contains(directory)) {
            throw new BadRequestException("허용되지 않은 디렉토리입니다. 허용: " + ALLOWED_DIRECTORIES);
        }
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new BadRequestException("파일명은 필수입니다.");
        }
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new BadRequestException("유효하지 않은 파일명입니다.");
        }
    }

    private void validateExtension(String extension) {
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("허용되지 않은 파일 형식입니다. 허용: " + ALLOWED_EXTENSIONS);
        }
    }

    public record PresignedUrlResponse(
        String presignedUrl,
        String imageUrl,
        String key,
        long maxFileSize
    ) {
    }
}
