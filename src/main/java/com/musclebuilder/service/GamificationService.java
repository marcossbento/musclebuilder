package com.musclebuilder.service;

import com.musclebuilder.config.GamificationProperties;
import com.musclebuilder.event.WorkoutCompletedEvent;
import com.musclebuilder.model.*;
import com.musclebuilder.repository.MissionCompletionRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GamificationService {
    private static final Logger logger = LoggerFactory.getLogger(GamificationService.class);

    private final WorkoutLogRepository workoutLogRepository;
    private final MissionCompletionRepository missionCompletionRepository;
    private final List<AchievementChecker> achievementCheckers;
    private final List<MissionChecker> missionCheckers;
    private final GamificationProperties gamificationProperties;

    @Autowired
    public GamificationService(
            WorkoutLogRepository workoutLogRepository,
            MissionCompletionRepository missionCompletionRepository,
            List<AchievementChecker> achievementCheckers,
            List<MissionChecker> missionCheckers,
            GamificationProperties gamificationProperties) {
        this.workoutLogRepository = workoutLogRepository;
        this.missionCompletionRepository = missionCompletionRepository;
        this.achievementCheckers = achievementCheckers;
        this.missionCheckers = missionCheckers;
        this.gamificationProperties = gamificationProperties;
    }

    @EventListener
    @Transactional
    public void handleWorkoutCompleted(WorkoutCompletedEvent event) {
        WorkoutLog completedLog = event.getWorkoutLog();
        User user = completedLog.getUser();

        awardXpForWorkout(user, completedLog);

        List<MissionChecker> completedMissions = missionCheckers.stream()
                .filter(checker -> checker.check(event).isPresent())
                .toList();

        long totalMissionXp = 0;
        if (!completedMissions.isEmpty()) {

            totalMissionXp = completedMissions.stream()
                    .mapToLong(checker -> checker.check(event).orElse(0L))
                    .sum();

            List<MissionCompletion> completionRecords = completedMissions.stream()
                    .map(checker -> new MissionCompletion(user, checker.getMissionId()))
                    .toList();

            missionCompletionRepository.saveAll(completionRecords);

            logger.info("Missões concluídas: {}. Recompensa total: {} XP",
                    completedMissions.stream().map(MissionChecker::getMissionId).collect(Collectors.joining(", ")),
                    totalMissionXp);
        }

        if (totalMissionXp > 0) {
            user.setExperiencePoints(user.getExperiencePoints() + totalMissionXp);
        }

        List<Achievement> newAchievements = checkAndAwardAchievements(user);
        event.addAchievements(newAchievements);

        checkLevelUp(user);

    }

    public List<Achievement> checkAndAwardAchievements(User user) {

        return achievementCheckers.stream()
                .map(checker -> checker.check(user))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public long calculateXpForWorkout(User user, Workout workout) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();

        long workoutCompletedTodayCount = workoutLogRepository.countByUserAndStatusAndCompletedAtAfter(user,
                WorkoutLogStatus.COMPLETED, startOfDay);

        long nextWorkoutNumber = workoutCompletedTodayCount + 1;

        double dailyXpModifier = getDailyXpModifier(nextWorkoutNumber);

        double estimatedVolume = workout.getWorkoutExercises().stream()
                .mapToDouble(we -> (we.getWeight() != null ? we.getWeight() : 0.0) * we.getSets() * we.getRepsPerSet())
                .sum();

        return calculateFinalXp(dailyXpModifier, estimatedVolume);
    }

    public void awardXpForWorkout(User user, WorkoutLog workoutLog) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        long workoutCompletedTodayCount = workoutLogRepository.countByUserAndStatusAndCompletedAtAfter(user,
                WorkoutLogStatus.COMPLETED, startOfDay);

        double dailyXpModifier = getDailyXpModifier(workoutCompletedTodayCount);

        double actualVolume = (workoutLog.getTotalVolume() != null) ? workoutLog.getTotalVolume() : 0.0;

        long xpGained = calculateFinalXp(dailyXpModifier, actualVolume);

        logger.info(
                "Concedendo {} XP para o utilizador {} (Modificador Diário: {}%, Volume Real: {})",
                xpGained,
                user.getEmail(),
                (int) (dailyXpModifier * 100),
                actualVolume);

        user.setExperiencePoints(user.getExperiencePoints() + xpGained);
        checkLevelUp(user);
    }

    // Cálculo de XP necessário para próximo nível, resultado exponencial conforme o
    // nível
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
        List<WorkoutLog> completedLogs = workoutLogRepository.findByUserAndStatusOrderByCompletedAtDesc(user,
                WorkoutLogStatus.COMPLETED);

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

    private long calculateFinalXp(double dailyXpModifier, double volume) {
        long xpPerWorkout = gamificationProperties.getXpPerWorkout();
        double xpPerVolumeUnit = gamificationProperties.getXpPerVolumeUnit();
        double volumeXpCapRatio = gamificationProperties.getVolumeXpCapRatio();

        long actualVolumeXp = (long) (volume * xpPerVolumeUnit);
        long volumeXpCap = (long) (xpPerWorkout * volumeXpCapRatio);
        long finalVolumeXp = Math.min(actualVolumeXp, volumeXpCap);

        long totalBaseXp = xpPerWorkout + finalVolumeXp;
        return (long) (totalBaseXp * dailyXpModifier);
    }

    private double getDailyXpModifier(long workoutCount) {
        if (workoutCount <= 1) {
            return gamificationProperties.getDailyModifier().getFirstWorkout();
        }
        if (workoutCount == 2) {
            return gamificationProperties.getDailyModifier().getSecondWorkout();
        }
        return gamificationProperties.getDailyModifier().getSubsequentWorkouts();
    }
}
