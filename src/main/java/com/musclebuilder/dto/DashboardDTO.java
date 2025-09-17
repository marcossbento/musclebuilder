package com.musclebuilder.dto;

import java.util.List;
import java.util.Optional;

public record DashboardDTO(
        UserLevelDTO userLevel,

        GamificationStatsDTO stats,

        List<MissionProgressDTO> activeMissions,

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

    public record RecommendedWorkoutDTO(
            Long workoutId,
            String name,
            String description
    ) {}
}
