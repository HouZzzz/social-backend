CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(32)  NOT NULL,
    password_hash VARCHAR(72)  NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_users_username_lower ON users (lower(username));

CREATE TABLE posts (
    id             BIGSERIAL PRIMARY KEY,
    author_id      BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    content        VARCHAR(280) NOT NULL,
    image_key      VARCHAR(255),
    likes_count    INT          NOT NULL DEFAULT 0,
    comments_count INT          NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT ck_posts_content_not_blank CHECK (btrim(content) <> ''),
    CONSTRAINT ck_posts_likes_non_negative CHECK (likes_count >= 0),
    CONSTRAINT ck_posts_comments_non_negative CHECK (comments_count >= 0)
);

-- дефолтная сортировка ленты ORDER BY created_at DESC, id DESC
CREATE INDEX ix_posts_feed ON posts (created_at DESC, id DESC);
-- Чипс мои посты
CREATE INDEX ix_posts_author ON posts (author_id, created_at DESC);

CREATE TABLE post_likes (
    post_id    BIGINT      NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
    user_id    BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (post_id, user_id)
);

-- чипс лайкнутые
CREATE INDEX ix_post_likes_user ON post_likes (user_id);

CREATE TABLE comments (
    id         BIGSERIAL PRIMARY KEY,
    post_id    BIGINT       NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
    author_id  BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    text       VARCHAR(500) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT ck_comments_text_not_blank CHECK (btrim(text) <> '')
);

CREATE INDEX ix_comments_post ON comments (post_id, created_at);
