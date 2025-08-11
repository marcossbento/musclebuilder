package com.musclebuilder.dto;

import com.musclebuilder.model.WorkoutLogStatus;

import java.time.LocalDateTime;
import java.util.List;

public record WorkoutLogResponseDTO(
        Long id,
        String workoutName,
        WorkoutLogStatus status,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        Integer durationMinutes,
        Double totalVolume,
        List<ExerciseLogResponseDTO> exerciseLogs
) {
    //Record aninhado para os detalhes de cada exercício registrado
    public record ExerciseLogResponseDTO(
       Long id,
       String exerciseName,

       Integer targetSets,
       Integer targetReps,

       Integer setsCompleted,
       String repsPerSet,
       Double weightUsed
    ) {}
}
