package com.musclebuilder.service;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;
import com.musclebuilder.repository.AchievementRepository;
import com.musclebuilder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final UserService userService;

    @Autowired
    public AchievementService(AchievementRepository achievementRepository, UserService userService) {
        this.achievementRepository = achievementRepository;
        this.userService = userService;
    }

    public List<Achievement> getCurrentUserAchievements() {
        User user = userService.findCurrentUser();

        return achievementRepository.findByUser(user);
    }

}
