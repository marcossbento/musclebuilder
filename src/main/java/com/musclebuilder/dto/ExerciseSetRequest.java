package com.musclebuilder.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ExerciseSetRequest(
        @NotNull(message = "O número de repetições é obrigatório") @Positive(message = "O número de repetições deve ser maior que zero") Integer reps,

        @NotNull(message = "O peso é obrigatório") @Positive(message = "O peso não pode ser negativo") Double weight) {
}
