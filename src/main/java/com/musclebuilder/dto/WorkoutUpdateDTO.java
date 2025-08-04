package com.musclebuilder.dto;

import com.musclebuilder.model.DifficultyLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record WorkoutUpdateDTO(
        @NotEmpty(message = "O nome do treino é obrigatório")
        @Size(min = 3, message = "O nome do treino deve ter pelo menos 3 caracteres")
        String name,

        String description,

        DifficultyLevel difficultyLevel,

        @Valid
        @NotEmpty(message = "O treino deve ter pelo menos um exercício")
        List<WorkoutExerciseCreateDTO> exercises
) {
}
