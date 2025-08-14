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

@Service
public class GamificationService {

    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final ExerciseLogRepository exerciseLogRepository;
    private final UserService userService;

    @Autowired
    public GamificationService(AchievementRepository achievementRepository, UserRepository userRepository, WorkoutLogRepository workoutLogRepository, ExerciseLogRepository exerciseLogRepository, UserService userService) {
        this.achievementRepository = achievementRepository;
        this.userRepository = userRepository;
        this.workoutLogRepository = workoutLogRepository;
        this.exerciseLogRepository = exerciseLogRepository;
        this.userService = userService;
    }

    public void checkAndAwardAchievements() {
        User currentUser = userService.findCurrentUser();

        checkFirstWorkoutAchievement(currentUser);
        checkTotalVolumeAchievement(currentUser);
    }

    private void checkFirstWorkoutAchievement(User user) {
        final String achievementName = "Primeiro Treino";

        if (!achievementRepository.existsByUserAndName(user, achievementName)) {
            long completedWorkouts = workoutLogRepository.countByUserAndStatus(user, WorkoutLogStatus.COMPLETED);
            if (completedWorkouts >= 1) {
                awardAchievement(user, achievementName, "Você completou seu primeiro treino. Bem-vindo à jornada!", "url_badge_1.png");
            }
        }
    }

    private void checkTotalVolumeAchievement(User user) {
        final String achievementName = "Clube dos 1000kg";

        if (!achievementRepository.existsByUserAndName(user, achievementName)) {
            double totalVolume = exerciseLogRepository.findTotalVolumeByUser(user);
            if (totalVolume >= 1000) {
                awardAchievement(user, achievementName, "Você levantou mais de 1000kg no total! Incrível!", "/badges/volume_1000kg.png");
            }
        }
    }

    private void awardAchievement(User user, String name, String description, String badgeUrl) {
        Achievement newAchievement = new Achievement();
        newAchievement.setUser(user);
        newAchievement.setName(name);
        newAchievement.setDescription(description);
        newAchievement.setBadgeUrl(badgeUrl);

        achievementRepository.save(newAchievement);
    }
}
