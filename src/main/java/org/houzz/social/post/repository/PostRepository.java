package org.houzz.social.post.repository;

import org.houzz.social.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = """
            SELECT p.id                AS "id",
                   p.content           AS "content",
                   p.image_key         AS "imageKey",
                   p.created_at        AS "createdAt",
                   p.likes_count       AS "likesCount",
                   p.comments_count    AS "commentsCount",
                   u.username          AS "authorUsername",
                   (pl.user_id IS NOT NULL) AS "isLikedByMe"
            FROM posts p
            JOIN users u ON u.id = p.author_id
            LEFT JOIN post_likes pl ON pl.post_id = p.id AND pl.user_id = :viewerId
            WHERE p.content ILIKE '%' || :search || '%'
              AND (:filter <> 'my'    OR p.author_id = :viewerId)
              AND (:filter <> 'liked' OR pl.user_id IS NOT NULL)
            ORDER BY p.created_at DESC, p.id DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<FeedRow> findFeed(@Param("viewerId") Long viewerId,
                           @Param("search") String search,
                           @Param("filter") String filter,
                           @Param("limit") int limit,
                           @Param("offset") int offset);

    @Query(value = """
            SELECT p.id                AS "id",
                   p.content           AS "content",
                   p.image_key         AS "imageKey",
                   p.created_at        AS "createdAt",
                   p.likes_count       AS "likesCount",
                   p.comments_count    AS "commentsCount",
                   u.username          AS "authorUsername",
                   (pl.user_id IS NOT NULL) AS "isLikedByMe"
            FROM posts p
            JOIN users u ON u.id = p.author_id
            LEFT JOIN post_likes pl ON pl.post_id = p.id AND pl.user_id = :viewerId
            WHERE p.id = :postId
            """, nativeQuery = true)
    FeedRow findFeedRowById(@Param("postId") Long postId,
                            @Param("viewerId") Long viewerId);
}
