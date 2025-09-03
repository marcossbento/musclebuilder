package com.musclebuilder.service.achievements;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;
import com.musclebuilder.model.WorkoutLogStatus;
import com.musclebuilder.repository.AchievementRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import com.musclebuilder.service.AchievementChecker;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FirstWorkoutAchievementChecker extends AbstractAchievementChecker implements AchievementChecker {

    private final WorkoutLogRepository workoutLogRepository;

    public FirstWorkoutAchievementChecker(AchievementRepository achievementRepository, WorkoutLogRepository workoutLogRepository) {
        super(achievementRepository);
        this.workoutLogRepository = workoutLogRepository;
    }

    @Override
    public Optional<Achievement> check(User user) {
        final String achievementName = "Primeiro Treino";

        if (!hasAchievement(user, achievementName)) {
            long completedWorkouts = workoutLogRepository.countByUserAndStatus(user, WorkoutLogStatus.COMPLETED);
            if (completedWorkouts >= 1) {
                Achievement awarded = awardAchievement(user, achievementName, "Você completou seu primeiro treino. Bem-vindo à jornada!", "assets/badges/badge_FirstWorkout.png");
                return Optional.of(awarded);
            }
        }

        return Optional.empty(); // Se não houver novas conquistas retorna vazio
    }
}
