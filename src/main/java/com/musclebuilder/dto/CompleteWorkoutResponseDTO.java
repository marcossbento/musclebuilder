package com.musclebuilder.dto;

import com.musclebuilder.model.Achievement;

import java.util.List;

public record CompleteWorkoutResponseDTO(
        WorkoutLogResponseDTO workoutLog,
        List<Achievement> newlyAwardedAchievements
) {}
