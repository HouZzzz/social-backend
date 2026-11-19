package org.houzz.social.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.houzz.social.auth.entity.User;

import java.time.Instant;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 280)
    private String content;

    @Column(name = "image_key", length = 255)
    private String imageKey;

    @Column(name = "likes_count", nullable = false)
    private int likesCount;

    @Column(name = "comments_count", nullable = false)
    private int commentsCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Post() {
    }

    public Post(User author, String content, String imageKey) {
        this.author = author;
        this.content = content;
        this.imageKey = imageKey;
        this.likesCount = 0;
        this.commentsCount = 0;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getImageKey() {
        return imageKey;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
