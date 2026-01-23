CREATE TABLE exercise_sets (
    id BIGSERIAL PRIMARY KEY,
    exercise_log_id BIGINT NOT NULL,
    reps INTEGER NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    order_index INTEGER NOT NULL,

    -- Constraint: Garante a integridade referencial (Filho aponta para Pai)
    -- ON DELETE CASCADE: Se apagar o Log, apaga as séries automaticamente
    CONSTRAINT fk_exercise_sets_log
        FOREIGN KEY (exercise_log_id)
        REFERENCES exercise_logs(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_exercise_sets_log_id ON exercise_sets(exercise_log_id);

-- Limpeza da Tabela Antiga
-- Como removemos o atributo 'repsPerSet' do Java, removemos a coluna do banco.
ALTER TABLE exercise_logs DROP COLUMN reps_per_set;