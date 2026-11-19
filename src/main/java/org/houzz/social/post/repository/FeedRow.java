package org.houzz.social.post.repository;

public interface FeedRow {

    Long getId();

    String getContent();

    String getImageKey();

    java.time.Instant getCreatedAt();

    int getLikesCount();

    int getCommentsCount();

    String getAuthorUsername();

    boolean getIsLikedByMe();
}
