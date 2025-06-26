-- Definição de tipos ENUM usados pelas entidades
CREATE TYPE difficultylevel AS ENUM ('BEGINNER','INTERMEDIATE','ADVANCED');
CREATE TYPE workoutstatus  AS ENUM ('ACTIVE','INACTIVE','COMPLETED');

-- Usuários
CREATE TABLE users (
  id          BIGSERIAL PRIMARY KEY,
  name        VARCHAR(255) NOT NULL,
  email       VARCHAR(255) NOT NULL UNIQUE,
  password    VARCHAR(255) NOT NULL,
  height      VARCHAR(255),
  weight      VARCHAR(255),
  goal        VARCHAR(255),
  created_at  TIMESTAMP   NOT NULL DEFAULT now(),
  updated_at  TIMESTAMP   NOT NULL DEFAULT now()
);

-- Exercícios
CREATE TABLE exercises (
  id              BIGSERIAL PRIMARY KEY,
  name            VARCHAR(255) NOT NULL UNIQUE,
  description     TEXT,
  muscle_group    VARCHAR(255),
  equipment       VARCHAR(255),
  difficulty_level VARCHAR(255),
  image_url       VARCHAR(255),
  created_at      TIMESTAMP   NOT NULL DEFAULT now(),
  updated_at      TIMESTAMP   NOT NULL DEFAULT now()
);

-- Treinos
CREATE TABLE workouts (
  id          BIGSERIAL PRIMARY KEY,
  title       VARCHAR(255) NOT NULL,
  description TEXT,
  status      VARCHAR(50),
  user_id     BIGINT     NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  created_at  TIMESTAMP   NOT NULL DEFAULT now(),
  updated_at  TIMESTAMP   NOT NULL DEFAULT now()
);

-- Associação treino - exercício
CREATE TABLE workout_exercises (
  id              BIGSERIAL PRIMARY KEY,
  workout_id      BIGINT    NOT NULL REFERENCES workouts(id) ON DELETE CASCADE,
  exercise_id     BIGINT    NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,
  sets            INTEGER   NOT NULL,
  reps_per_set    INTEGER   NOT NULL,
  weight          DOUBLE PRECISION,
  rest_seconds    INTEGER,
  order_position  INTEGER
);

