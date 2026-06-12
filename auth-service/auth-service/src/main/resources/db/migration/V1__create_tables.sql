CREATE TABLE user_credentials (
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    senha      VARCHAR(255),
    github_id  BIGINT UNIQUE,
    github_login VARCHAR(255),
    role       VARCHAR(20) NOT NULL DEFAULT 'USER'
);