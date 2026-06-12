CREATE TABLE rooms (
    id           BIGSERIAL PRIMARY KEY,
    nome         VARCHAR(255) NOT NULL UNIQUE,
    capacidade   INTEGER NOT NULL,
    localizacao  VARCHAR(255),
    ativa        BOOLEAN NOT NULL DEFAULT TRUE
);