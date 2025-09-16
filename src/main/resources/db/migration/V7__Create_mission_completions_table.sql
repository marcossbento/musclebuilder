CREATE TABLE mission_completions (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    mission_id VARCHAR(255) NOT NULL,

    completed_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_mission_completions_to_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_mission_completions_user_id ON mission_completions(user_id);