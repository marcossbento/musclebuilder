package com.musclebuilder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDTO {

    private Long id;

    private String name;

    private String description;

    private String muscleGroup;

    private String equipment;

    private String difficultyLevel;

    private String imageUrl;
}