package com.musclebuilder.service;

import com.musclebuilder.event.WorkoutCompletedEvent;
import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;
import com.musclebuilder.model.WorkoutLog;
import com.musclebuilder.model.WorkoutLogStatus;
import com.musclebuilder.repository.WorkoutLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GamificationService {
    private static final Logger logger = LoggerFactory.getLogger(GamificationService.class);

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

    @EventListener
    public void handleWorkoutCompleted(WorkoutCompletedEvent event) {

        WorkoutLog completedLog = event.getWorkoutLog();
        User user = completedLog.getUser();

        awardXpForWorkout(user, completedLog);
        List<Achievement> newAchievements = checkAndAwardAchievements(user);

        event.addAchievements(newAchievements);

    }

    public List<Achievement> checkAndAwardAchievements(User user) {

        return achievementCheckers.stream()
                .map(checker -> checker.check(user))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public void awardXpForWorkout(User user, WorkoutLog workoutLog) {
        long xpGained = XP_PER_WORKOUT;
        if (workoutLog.getTotalVolume() != null && workoutLog.getTotalVolume() > 0) {
            xpGained += (long) (workoutLog.getTotalVolume() * XP_PER_VOLUME_UNIT);
        }

        logger.info("Concedendo {} XP para o usuário {}", xpGained, user.getEmail());
        user.setExperiencePoints(user.getExperiencePoints() + xpGained);

        checkLevelUp(user);
    }

    // Cálculo de XP necessário para próximo nível, resultado exponencial de acordo com o nível
    public long getTotalXpForLevel(int level) {
        if (level <= 1) {
            return 0;
        }
        return (long) (500 * Math.pow(level - 1, 2) + 1000L * (level - 1));
    }

    public void checkLevelUp(User user) {
        long xpNeededForNextLevel = getTotalXpForLevel(user.getLevel() + 1);

        while (user.getExperiencePoints() >= xpNeededForNextLevel) {
            user.setLevel(user.getLevel() + 1);

            logger.info("PARABÉNS! {} subiu para o nível {}!", user.getName(), user.getLevel());

            xpNeededForNextLevel = getTotalXpForLevel(user.getLevel() + 1);
        }
    }

    public int calculateWorkoutStreak(User user) {
        List<WorkoutLog> completedLogs = workoutLogRepository.findByUserAndStatusOrderByCompletedAtDesc(user, WorkoutLogStatus.COMPLETED);

        if (completedLogs.isEmpty()) {
            return 0;
        }

        List<LocalDate> uniqueWorkoutDates = completedLogs.stream()
                .map(log -> log.getCompletedAt().toLocalDate())
                .distinct()
                .collect(Collectors.toList());

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate mostRecentWorkoutDate = uniqueWorkoutDates.get(0);

        if (!mostRecentWorkoutDate.isEqual(today) && !mostRecentWorkoutDate.isEqual(yesterday)) {
            return 0;
        }

        int streak = 1;
        for (int i = 1; i < uniqueWorkoutDates.size(); i++) {
            LocalDate currentDay = uniqueWorkoutDates.get(i - 1);
            LocalDate previousDay = uniqueWorkoutDates.get(i);

            if (previousDay.isEqual(currentDay.minusDays(1))) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }
}
