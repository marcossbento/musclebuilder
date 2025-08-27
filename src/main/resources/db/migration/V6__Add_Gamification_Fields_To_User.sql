-- Adiciona colunas para armazenar a experiência (XP) e o nível do usuário.
ALTER TABLE users ADD COLUMN experience_points BIGINT NOT NULL DEFAULT 0;
ALTER TABLE users ADD COLUMN user_level INTEGER NOT NULL DEFAULT 1;

-- Comentários para documentar a decisão no próprio script.
COMMENT ON COLUMN users.experience_points IS 'Armazena o total de pontos de experiência acumulados pelo usuário.';
COMMENT ON COLUMN users.user_level IS 'Armazena o nível atual do usuário no sistema de gamificação.';