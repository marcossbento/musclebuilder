package com.musclebuilder.dto;

import com.musclebuilder.model.DifficultyLevel;
import com.musclebuilder.model.MuscleGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExerciseDTO (

        Long id,

        @NotBlank(message = "O nome do exercício é obrigatório")
        String name,

        String description,

        MuscleGroup muscleGroup,

        String equipment,

        @NotNull(message = "Nível de dificuldade é obrigatório")
        DifficultyLevel difficultyLevel,

        String imageUrl
) {}