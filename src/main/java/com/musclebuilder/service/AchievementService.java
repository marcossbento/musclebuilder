package com.musclebuilder.service;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;
import com.musclebuilder.repository.AchievementRepository;
import com.musclebuilder.service.security.SecurityContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final SecurityContextService securityContextService;

    @Autowired
    public AchievementService(AchievementRepository achievementRepository, SecurityContextService securityContextService) {
        this.achievementRepository = achievementRepository;
        this.securityContextService = securityContextService;
    }

    public List<Achievement> getCurrentUserAchievements() {
        User user = securityContextService.findCurrentUser();

        return achievementRepository.findByUser(user);
    }

}
