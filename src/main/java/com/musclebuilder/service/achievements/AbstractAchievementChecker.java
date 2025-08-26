package com.musclebuilder.service.achievements;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;
import com.musclebuilder.repository.AchievementRepository;

import java.time.LocalDateTime;

public abstract class AbstractAchievementChecker {

    protected final AchievementRepository achievementRepository;

    public AbstractAchievementChecker(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    protected boolean hasAchievement(User user, String name) {
        return achievementRepository.existsByUserAndName(user, name);
    }

    protected Achievement awardAchievement(User user, String name, String description, String badgeUrl) {
        Achievement newAchievement = new Achievement();
        newAchievement.setUser(user);
        newAchievement.setName(name);
        newAchievement.setDescription(description);
        newAchievement.setBadgeUrl(badgeUrl);
        newAchievement.setEarnedAt(LocalDateTime.now());

        return achievementRepository.save(newAchievement);
    }
}
