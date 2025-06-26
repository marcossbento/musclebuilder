package com.musclebuilder.dto;

import jakarta.validation.constraints.NotBlank;

public record StartWorkoutRequest(
        Long workoutId,

        @NotBlank(message = "O nome do treino é obrigatório")
        String workoutName
) {}
