CREATE TABLE workout_logs (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    workout_id          BIGINT        REFERENCES workouts(id) ON DELETE SET NULL,
    workout_name        VARCHAR(255)  NOT NULL,
    started_at          TIMESTAMP     NOT NULL,
    completed_at        TIMESTAMP,
    duration_minutes    INTEGER,
    total_volume        DOUBLE PRECISION,
    notes               TEXT,
    status              VARCHAR(20)   NOT NULL DEFAULT 'IN_PROGRESS',
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE exercise_logs (
    id                  BIGSERIAL PRIMARY KEY,
    workout_log_id      BIGINT        NOT NULL REFERENCES workout_logs(id) ON DELETE CASCADE,
    exercise_id         BIGINT        NOT NULL REFERENCES exercises(id) ON DELETE RESTRICT,
    exercise_name       VARCHAR(255)  NOT NULL,
    sets_completed      INTEGER       NOT NULL,
    reps_per_set        VARCHAR(100)  NOT NULL, -- Ex: "12,10,8"
    weight_used         DOUBLE PRECISION,
    rest_seconds        INTEGER,
    volume              DOUBLE PRECISION,
    max_weight          DOUBLE PRECISION,
    total_reps          INTEGER,
    order_position      INTEGER,
    notes               TEXT,
    difficulty_rating   INTEGER CHECK (difficulty_rating >= 1 AND difficulty_rating <= 10),
    started_at          TIMESTAMP,
    completed_at        TIMESTAMP,
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- Índices para otimização de performance
CREATE INDEX idx_workout_logs_user_id ON workout_logs(user_id);
CREATE INDEX idx_workout_logs_status ON workout_logs(status);
CREATE INDEX idx_workout_logs_started_at ON workout_logs(started_at);
CREATE INDEX idx_exercise_logs_workout_log_id ON exercise_logs(workout_log_id);
CREATE INDEX idx_exercise_logs_exercise_id ON exercise_logs(exercise_id);
CREATE INDEX idx_exercise_logs_user_exercise ON exercise_logs(workout_log_id, exercise_id);

-- Constraint para garantir que status seja válido
ALTER TABLE workout_logs ADD CONSTRAINT chk_workout_log_status
    CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'CANCELLED'));
