package com.musclebuilder.service.achievements;

import com.musclebuilder.model.User;
import com.musclebuilder.model.WorkoutLogStatus;
import com.musclebuilder.repository.AchievementRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import com.musclebuilder.service.AchievementChecker;
import org.springframework.stereotype.Component;

@Component
public class FirstWorkoutAchievementChecker extends AbstractAchievementChecker implements AchievementChecker {

    private final WorkoutLogRepository workoutLogRepository;

    public FirstWorkoutAchievementChecker(AchievementRepository achievementRepository, WorkoutLogRepository workoutLogRepository) {
        super(achievementRepository);
        this.workoutLogRepository = workoutLogRepository;
    }

    @Override
    public void check(User user) {
        final String achievementName = "Primeiro Treino";

        if (!hasAchievement(user, achievementName)) {
            long completedWorkouts = workoutLogRepository.countByUserAndStatus(user, WorkoutLogStatus.COMPLETED);
            if (completedWorkouts >= 1) {
                awardAchievement(user, achievementName, "Você completou seu primeiro treino. Bem-vindo à jornada!", "url_badge_1.png");
            }
        }
    }
}
