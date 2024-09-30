DROP TABLE IF EXISTS posts;

DROP TABLE IF EXISTS post_tags;

CREATE TABLE posts
(
    id         BIGINT AUTO_INCREMENT,
    title      TEXT,
    content    TEXT,
    category   TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE post_tags
(
    post_id BIGINT,
    tag_name varchar(255),
    PRIMARY KEY (post_id, tag_name),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);