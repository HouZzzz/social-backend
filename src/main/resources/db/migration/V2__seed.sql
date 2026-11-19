-- Пароль обоих пользователей: password123
INSERT INTO users (id, username, password_hash) VALUES
    (1, 'ilya_ilyich',   '$2a$10$sG1vG/Z5VJ0fXWOs9QkeAOU5K1/V9vhUkpeTjZytzjA8EpFY59u4m'),
    (2, 'ivan_ivanov', '$2a$10$sG1vG/Z5VJ0fXWOs9QkeAOU5K1/V9vhUkpeTjZytzjA8EpFY59u4m');

SELECT setval('users_id_seq', (SELECT max(id) FROM users));

INSERT INTO posts (id, author_id, content, likes_count, comments_count, created_at) VALUES
    (1, 1, 'Первый пост', 2, 2, now() - interval '3 hours'),
    (2, 2, 'Второй пост', 1, 0, now() - interval '2 hours'),
    (3, 1, 'Третий пост', 0, 0, now() - interval '1 hour'),
    (4, 2, 'Четвертый пост', 1, 0, now() - interval '30 minutes');

SELECT setval('posts_id_seq', (SELECT max(id) FROM posts));

INSERT INTO post_likes (post_id, user_id) VALUES
    (1, 1),
    (1, 2),
    (2, 1),
    (4, 1);

INSERT INTO comments (id, post_id, author_id, text, created_at) VALUES
    (1, 1, 2, 'комментарий',   now() - interval '2 hours 30 minutes'),
    (2, 1, 1, 'комментарий комментарию', now() - interval '2 hours 10 minutes');

SELECT setval('comments_id_seq', (SELECT max(id) FROM comments));
