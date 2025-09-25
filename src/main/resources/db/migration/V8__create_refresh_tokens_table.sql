CREATE TABLE refresh_tokens (
    id          BIGSERIAL PRIMARY KEY,
    token       VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP    NOT NULL,
    user_id     BIGINT       NOT NULL
);

ALTER TABLE refresh_tokens
ADD CONSTRAINT fk_refresh_tokens_to_users
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);