package org.houzz.social.comment.repository;

import org.houzz.social.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            SELECT c FROM Comment c
            JOIN FETCH c.author
            WHERE c.post.id = :postId
            ORDER BY c.createdAt ASC, c.id ASC
            """)
    List<Comment> findAllByPostId(@Param("postId") Long postId);

    // atomic
    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = p.commentsCount + 1 WHERE p.id = :postId")
    int incrementCommentsCount(@Param("postId") Long postId);
}
