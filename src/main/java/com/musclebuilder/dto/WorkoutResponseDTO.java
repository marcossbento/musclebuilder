package com.musclebuilder.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.musclebuilder.model.DifficultyLevel;
import com.musclebuilder.model.WorkoutStatus;
import com.musclebuilder.model.WorkoutType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record WorkoutResponseDTO(

    Long id,

    @NotBlank(message = "O nome do treino é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    String name,

    String description,

    WorkoutType workoutType,

    Long userId,

    Integer weekNumber,

    Integer dayNumber,

    WorkoutStatus workoutStatus,

    Integer estimatedDurationMinutes,

    DifficultyLevel difficultyLevel,

    List<WorkoutExerciseDTO> exercises,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt

    ) {
    // Record aninhado para exercícios dentro do treino
    public record WorkoutExerciseDTO (
        Long id,

        @NotNull(message = "O ID do exercício é obrigatório")
        Long exerciseId,

        String exerciseName,

        @NotNull(message = "O número de séries é obrigatório")
        Integer sets,

        @NotNull(message = "O número de repetições é obrigatório")
        Integer repsPerSet,

        Double weight,

        Integer restSeconds,

        Integer orderPosition
    ) {}
}
