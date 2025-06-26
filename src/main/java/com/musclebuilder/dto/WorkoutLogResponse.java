package com.musclebuilder.dto;

import com.musclebuilder.model.WorkoutLogStatus;

import java.time.LocalDateTime;
import java.util.List;

public record WorkoutLogResponse(
        Long id,
        String workoutName,
        WorkoutLogStatus status,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        Long durationMinutes,
        Double totalVolume,
        List<ExerciseLogDetails> exerciseLogs
) {
    //Record aninhado para os detalhes de cada exercício registrado
    public record ExerciseLogDetails(
       Long id,
       Long exerciseId,
       String exerciseName,
       Integer setsCompleted,
       String repsPerSet,
       Double weightUsed,
       String notes
    ) {}
}
