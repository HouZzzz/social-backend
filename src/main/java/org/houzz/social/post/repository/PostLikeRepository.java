package org.houzz.social.post.repository;

import org.houzz.social.common.exception.NotFoundException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;


@Repository
public class PostLikeRepository {


    // ins - returns 1 IF inserted (like not existed yet)
    // del - deletes previous like row IF not inserted
    // delta +1 if liked, -1 if unliked
    // atomic update of normalized likes counter
    private static final String TOGGLE_SQL = """
            WITH ins AS (
                INSERT INTO post_likes (post_id, user_id)
                VALUES (:postId, :userId)
                ON CONFLICT (post_id, user_id) DO NOTHING
                RETURNING 1
            ), del AS (
                DELETE FROM post_likes
                WHERE post_id = :postId
                  AND user_id = :userId
                  AND NOT EXISTS (SELECT 1 FROM ins)
                RETURNING 1
            ), delta AS (
                SELECT COALESCE((SELECT count(*) FROM ins), 0)
                     - COALESCE((SELECT count(*) FROM del), 0) AS d
            )
            UPDATE posts
               SET likes_count = likes_count + (SELECT d FROM delta)
             WHERE id = :postId
            RETURNING likes_count AS "likesCount", (SELECT d FROM delta) AS "delta"
            """;

    private final JdbcClient jdbcClient;

    public PostLikeRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public ToggleResult toggle(Long postId, Long userId) {
        return jdbcClient.sql(TOGGLE_SQL)
                .param("postId", postId)
                .param("userId", userId)
                .query((rs, rowNum) -> new ToggleResult(
                        rs.getInt("likesCount"),
                        rs.getInt("delta") > 0))
                .optional()
                .orElseThrow(() -> new NotFoundException("Пост не найден"));
    }

    public record ToggleResult(int likesCount, boolean likedByMe) {
    }
}
