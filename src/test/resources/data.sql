DELETE FROM posts;

ALTER TABLE posts ALTER COLUMN id RESTART WITH 1;

INSERT INTO posts (title, content, category, created_at, updated_at)
VALUES ('Updated Post 1', 'This is the content for post 1', 'Category 1', '2023-06-14 21:14:00', '2024-09-29 07:16:32'),
       ('New Post 2', 'This is the content for post 2', 'Category 2', '2023-11-17 12:41:32', '2023-11-17 12:41:32'),
       ('New Post 3', 'This is the content for post 3', 'Category 3', '2024-09-29 21:15:30', '2024-09-29 21:15:30');

INSERT INTO post_tags (post_id, tag_name)
VALUES (1, 'TECHNOLOGY'),
       (1, 'INTERNET'),
       (2, 'JAVA'),
       (2, 'PROGRAMMING'),
       (2, 'COMPUTER');