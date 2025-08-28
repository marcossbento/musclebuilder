package com.musclebuilder.service;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;
import com.musclebuilder.model.WorkoutLog;
import com.musclebuilder.model.WorkoutLogStatus;
import com.musclebuilder.repository.AchievementRepository;
import com.musclebuilder.repository.ExerciseLogRepository;
import com.musclebuilder.repository.UserRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GamificationService {
    // Constantes de XP distribuídos por ação do user
    private static final long XP_PER_WORKOUT = 100;
    private static final double XP_PER_VOLUME_UNIT = 0.5;
    private static final int BASE_XP_FOR_NEXT_LEVEL = 1000;

    private final WorkoutLogRepository workoutLogRepository;
    private final List<AchievementChecker> achievementCheckers;
    private final UserService userService;

    @Autowired
    public GamificationService(WorkoutLogRepository workoutLogRepository, List<AchievementChecker> achievementCheckers, UserService userService) {
        this.workoutLogRepository = workoutLogRepository;
        this.achievementCheckers = achievementCheckers;
        this.userService = userService;
    }

    public List<Achievement> checkAndAwardAchievements() {
        User currentUser = userService.findCurrentUser();

        return achievementCheckers.stream()
                .map(checker -> checker.check(currentUser))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public void awardXpForWorkout(User user, WorkoutLog workoutLog) {
        long xpGained = XP_PER_WORKOUT;
        if (workoutLog.getTotalVolume() != null && workoutLog.getTotalVolume() > 0) {
            xpGained += (long) (workoutLog.getTotalVolume() * XP_PER_VOLUME_UNIT);
        }

        user.setExperiencePoints(user.getExperiencePoints() + xpGained);

        checkLevelUp(user);
    }

    // Cálculo de XP necessário para próximo nível, resultado exponencial de acordo com o nível
    public long getXpForLevel(int level) {
        if (level <= 1) {
            return BASE_XP_FOR_NEXT_LEVEL;
        }
        return (long) (BASE_XP_FOR_NEXT_LEVEL * Math.pow(level, 1.5));
    }

    public void checkLevelUp(User user) {
        long xpForNextLevel = getXpForLevel(user.getLevel() + 1);

        if (user.getExperiencePoints() >= xpForNextLevel) {
            user.setLevel(user.getLevel() + 1);
            // TODO: Conquita por subir de nível.
        }
    }

    public int calculateWorkoutStreak(User user) {
        List<WorkoutLog> logs = workoutLogRepository.findByUserOrderByStartedAtDesc(user, null).getContent();

        if (logs.isEmpty()) {
            return 0;
        }

        int streak = 0;
        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = today.with(DayOfWeek.MONDAY);

        LocalDate lastWorkoutDate = logs.get(0).getStartedAt().toLocalDate();
        if (lastWorkoutDate.isAfter(startOfThisWeek.minusDays(1)) || lastWorkoutDate.isEqual(startOfThisWeek.minusDays(1))) {
            streak = 1;
        } else if (lastWorkoutDate.isAfter(startOfThisWeek.minusWeeks(1).minusDays(1))) {
            streak = 1;
        } else {
            return 0;
        }

        for (int i = 1; i < logs.size(); i++) {
            LocalDate currentWorkoutDate = logs.get(i-1).getStartedAt().toLocalDate();
            LocalDate previousWorkoutDate = logs.get(i).getStartedAt().toLocalDate();

            long weeksBetween = ChronoUnit.WEEKS.between(previousWorkoutDate, currentWorkoutDate);

            if (weeksBetween <= 1) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }
}
