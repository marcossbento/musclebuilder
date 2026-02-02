package com.musclebuilder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record LogExerciseRequest(
                @NotNull(message = "O ID do exercício é obrigatório") Long exerciseId,

                @NotEmpty(message = "É necessário informar pelo menos uma série") @jakarta.validation.Valid List<ExerciseSetRequest> sets,

                String notes) {
}
