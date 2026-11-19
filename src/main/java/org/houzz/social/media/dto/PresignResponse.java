package org.houzz.social.media.dto;

public record PresignResponse(
        String uploadUrl,
        String objectKey,
        String publicUrl,
        long expiresInSeconds
) {
}
