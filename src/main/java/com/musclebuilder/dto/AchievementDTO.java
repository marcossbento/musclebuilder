package com.musclebuilder.dto;

import java.time.LocalDateTime;

public record AchievementDTO(
        String name,
        String description,
        String badgeUrl,
        LocalDateTime earnedAt
) {}
