-- Renomeia a coluna 'title' para 'name' para corresponder ao nome do campo na entidade.
ALTER TABLE workouts RENAME COLUMN title TO name;

-- Adiciona as colunas para periodização que estavam faltando.
ALTER TABLE workouts ADD COLUMN week_number INTEGER;
ALTER TABLE workouts ADD COLUMN day_number INTEGER;

-- Adiciona a coluna para o nível de dificuldade.
ALTER TABLE workouts ADD COLUMN difficulty_level VARCHAR(255);

-- 4. ADICIONA AS COLUNAS RESTANTES QUE ESTAVAM FALTANDO.
ALTER TABLE workouts ADD COLUMN workout_type VARCHAR(255);
ALTER TABLE workouts ADD COLUMN estimated_duration_minutes INTEGER;