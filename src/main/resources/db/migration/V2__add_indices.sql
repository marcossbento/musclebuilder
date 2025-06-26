-- Índices para buscas frequentes
CREATE INDEX idx_users_email       ON users(email);
CREATE INDEX idx_exercises_name    ON exercises(name);
CREATE INDEX idx_workouts_user     ON workouts(user_id);
CREATE INDEX idx_wkex_wk_ex        ON workout_exercises(workout_id, exercise_id);
