package com.musclebuilder.dto;

import com.musclebuilder.model.DifficultyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDTO {

    private Long id;

    @NotBlank(message = "O nome do exercício é obrigatório")
    private String name;

    private String description;

    @NotBlank(message = "Grupo muscular é obrigatório")
    private String muscleGroup;

    private String equipment;

    @NotNull(message = "Nível de dificuldade é obrigatório")
    private DifficultyLevel difficultyLevel;

    private String imageUrl;
}