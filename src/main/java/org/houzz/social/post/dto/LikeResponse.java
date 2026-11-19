package org.houzz.social.post.dto;

public record LikeResponse(
        Long postId,
        int likesCount,
        boolean isLikedByMe
) {
}
