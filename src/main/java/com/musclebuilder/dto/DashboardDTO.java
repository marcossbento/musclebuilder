package com.musclebuilder.dto;

import java.util.Optional;

public record DashboardDTO(
        UserLevelDTO userLevel,

        GamificationStatsDTO stats,

        WeeklyMissionDTO weeklyMission,

        Optional<RecommendedWorkoutDTO> recommendedWorkout,

        Optional<AchievementDTO> lastAchievement
) {

    public record UserLevelDTO(
        int level,
        long currentXp,
        long xpForNextLevel,
        double progressPercentage
    ) {}

    public record GamificationStatsDTO(
            long totalWorkouts,
            double totalVolume,
            int streak
    ) {}

    public record WeeklyMissionDTO(
            String title,
            int completed,
            int goal
    ) {}

    public record RecommendedWorkoutDTO(
            Long workoutId,
            String name,
            String description
    ) {}
}
