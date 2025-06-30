package com.musclebuilder.dto;

public record ProgressSummaryDTO(
        long totalWorkouts,
        double totalVolume,
        String mostFrequentExercise
) {}
