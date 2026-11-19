package org.houzz.social.media.service;

import jakarta.annotation.PostConstruct;
import org.houzz.social.media.dto.PresignResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
public class S3Service {

    private static final Logger log = LoggerFactory.getLogger(S3Service.class);

    private static final Map<String, String> CONTENT_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/jpg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final String bucket;
    private final String publicBaseUrl;
    private final Duration presignTtl;

    public S3Service(S3Client s3Client,
                     S3Presigner presigner,
                     @Value("${s3.bucket}") String bucket,
                     @Value("${s3.public-base-url}") String publicBaseUrl,
                     @Value("${s3.presign-ttl-minutes}") long presignTtlMinutes) {
        this.s3Client = s3Client;
        this.presigner = presigner;
        this.bucket = bucket;
        this.publicBaseUrl = publicBaseUrl.endsWith("/")
                ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                : publicBaseUrl;
        this.presignTtl = Duration.ofMinutes(presignTtlMinutes);
    }

    @PostConstruct
    void initBucket() {
        try {
            if (!bucketExists()) {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
                log.info("Создан bucket {}", bucket);
            }
            applyPublicReadPolicy();
        } catch (SdkException e) {
            log.warn("Не удалось инициализировать bucket {}: {}", bucket, e.getMessage());
        }
    }

    public PresignResponse presignUpload(Long userId, String contentType) {
        String extension = CONTENT_TYPES.getOrDefault(contentType.toLowerCase(), "jpg");
        String objectKey = "posts/" + userId + "/" + UUID.randomUUID() + "." + extension;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();

        var presigned = presigner.presignPutObject(PutObjectPresignRequest.builder()
                .signatureDuration(presignTtl)
                .putObjectRequest(objectRequest)
                .build());

        return new PresignResponse(
                presigned.url().toString(),
                objectKey,
                publicUrl(objectKey),
                presignTtl.toSeconds()
        );
    }

    public boolean exists(String objectKey) {
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucket).key(objectKey).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    public String publicUrl(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }
        return publicBaseUrl + "/" + bucket + "/" + objectKey;
    }

    private boolean bucketExists() {
        try {
            s3Client.headBucket(builder -> builder.bucket(bucket));
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }

    private void applyPublicReadPolicy() {
        String policy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {"AWS": ["*"]},
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(bucket);

        s3Client.putBucketPolicy(PutBucketPolicyRequest.builder()
                .bucket(bucket)
                .policy(policy)
                .build());
    }
}
