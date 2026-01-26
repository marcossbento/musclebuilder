package com.musclebuilder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record LogExerciseRequest(
                @NotNull(message = "O ID do exercício é obrigatório") Long exerciseId,

                @NotEmpty(message = "É necessário informar pelo menos uma série") @Positive(message = "O número de séries deve ser maior que zero") List<ExerciseSetRequest> sets,

                String notes) {
}
