package com.musclebuilder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LogExerciseRequest(
        @NotNull(message = "O ID do exercício é obrigatório")
        Long exerciseId,

        @NotNull(message = "O número de séries completas é obrigatório")
        @Positive(message = "O número de séries completas deve ser positivo")
        Integer setsCompleted,

        @NotBlank(message = "As repetições por série são obrigatórias")
        String repsPerSet,

        Double weightUsed,

        String notes
) {
}
