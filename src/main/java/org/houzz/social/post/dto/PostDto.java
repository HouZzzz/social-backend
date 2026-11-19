package org.houzz.social.post.dto;

import java.time.Instant;

public record PostDto(
        Long id,
        String content,
        String authorUsername,
        Instant createdAt,
        int likesCount,
        int commentsCount,
        boolean isLikedByMe,
        String imageUrl
) {
}
