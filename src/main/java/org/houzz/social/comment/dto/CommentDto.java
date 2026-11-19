package org.houzz.social.comment.dto;

import java.time.Instant;

public record CommentDto(
        Long id,
        String text,
        String authorUsername,
        Instant createdAt
) {
}
