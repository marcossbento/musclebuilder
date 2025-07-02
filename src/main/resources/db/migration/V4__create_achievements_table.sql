CREATE TABLE achievements (
    id                      BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    achievement_name        VARCHAR(255) NOT NULL,
    achievement_description TEXT NOT NULL,
    badge_url               VARCHAR(255),
    earned_at               TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ÍNDICES (OTIMIZAÇÃO DE PERFORMANCE)
CREATE INDEX idx_achievements_user_id ON achievements(user_id);


-- CONSTRAINT DE UNICIDADE
-- Garante a integridade dos dados, impedindo que a mesma conquista seja
-- concedida duas vezes para o mesmo usuário.
ALTER TABLE achievements ADD CONSTRAINT uk_user_achievement UNIQUE (user_id, achievement_name);