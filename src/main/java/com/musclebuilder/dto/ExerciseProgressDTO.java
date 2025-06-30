package com.musclebuilder.dto;

import java.time.LocalDate;

public record ExerciseProgressDTO(
        LocalDate date,
        double maxWeight,
        double volume
) {}
