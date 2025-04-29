package com.musclebuilder.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDTO {

    private Long id;

    @NotBlank(message = "O nome do treino é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String name;

    private String description;

    private String workoutType;

    private Long userId;

    private Integer weekNumber;

    private Integer dayNumber;

    private String status;

    private Integer estimatedDurationMinutes;

    private String difficultyLevel;

    private List<WorkoutExerciseDTO> exercises = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // DTO interno para representar os exercícios do treino
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkoutExerciseDTO {
        private Long id;

        @NotNull(message = "O ID do exercício é obrigatório")
        private Long exerciseId;

        private String exerciseName;

        @NotNull(message = "O número de séries é obrigatório")
        private Integer sets;

        @NotNull(message = "O número de repetições é obrigatório")
        private Integer repsPerSet;

        private Double weight;

        private Integer restSeconds;

        private Integer orderPosition;
    }
}
