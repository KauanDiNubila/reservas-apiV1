CREATE TABLE bookings (
    id         BIGSERIAL PRIMARY KEY,
    room_id    BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    inicio     TIMESTAMP NOT NULL,
    fim        TIMESTAMP NOT NULL,
    status     VARCHAR(20) NOT NULL DEFAULT 'ATIVA'
);