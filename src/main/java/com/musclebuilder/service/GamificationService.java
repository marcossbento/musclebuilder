package com.musclebuilder.service;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;
import com.musclebuilder.model.WorkoutLogStatus;
import com.musclebuilder.repository.AchievementRepository;
import com.musclebuilder.repository.ExerciseLogRepository;
import com.musclebuilder.repository.UserRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GamificationService {

    private final List<AchievementChecker> achievementCheckers;
    private final UserService userService;

    @Autowired
    public GamificationService(List<AchievementChecker> achievementCheckers, UserService userService) {
        this.achievementCheckers = achievementCheckers;
        this.userService = userService;
    }

    public void checkAndAwardAchievements() {
        User currentUser = userService.findCurrentUser();

        for (AchievementChecker checker : achievementCheckers) {
            checker.check(currentUser);
        }
    }
}
