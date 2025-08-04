package com.musclebuilder.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WorkoutExerciseCreateDTO(
      @NotNull(message = "O ID do exercicio é obrigatório")
      Long exerciseId,

      @NotNull(message = "O número de séries é obrigatório")
      @Min(value = 1, message = "Deve haver pelo menos 1 série")
      Integer sets,

      @NotNull(message = "O número de repetições é obrigatório")
      @Min(value = 1, message = "Deve haver pelo menos 1 repetição")
      Integer repsPerSet,

      Double weight,

      Integer restSeconds
) {}
