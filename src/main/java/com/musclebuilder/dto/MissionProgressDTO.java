package com.musclebuilder.dto;

public record MissionProgressDTO(
        String description,
        long xpReward,
        long goal,
        long currentProgress
) {
}
