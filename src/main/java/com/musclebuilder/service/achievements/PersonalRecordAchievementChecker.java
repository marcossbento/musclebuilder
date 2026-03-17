package com.musclebuilder.service.achievements;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;
import com.musclebuilder.repository.AchievementRepository;
import com.musclebuilder.service.AchievementChecker;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersonalRecordAchievementChecker extends AbstractAchievementChecker implements AchievementChecker {

    private static final String ACHIEVEMENT_NAME = "Recordista Pessoal";

    public PersonalRecordAchievementChecker(AchievementRepository achievementRepository) {
        super(achievementRepository);
    }

    @Override
    public Optional<Achievement> check(User user) {
        // A conquista "Recordista Pessoal" é concedida diretamente via awardIfEligible()
        // durante o fluxo de handleWorkoutCompleted no GamificationService,
        // pois a detecção de PR requer contexto do treino atual.
        return Optional.empty();
    }

    /**
     * Método invocado diretamente pelo GamificationService
     * quando um PR é detectado durante o complete do treino.
     */
    public Optional<Achievement> awardIfEligible(User user) {
        if (hasAchievement(user, ACHIEVEMENT_NAME)) {
            return Optional.empty();
        }

        Achievement awarded = awardAchievement(
                user,
                ACHIEVEMENT_NAME,
                "Você quebrou seu recorde pessoal de carga! Continue superando seus limites!",
                "assets/badges/badge_PersonalRecord.png");

        return Optional.of(awarded);
    }
}
