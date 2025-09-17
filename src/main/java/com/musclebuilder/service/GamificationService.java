package com.musclebuilder.service;

import com.musclebuilder.event.WorkoutCompletedEvent;
import com.musclebuilder.model.*;
import com.musclebuilder.repository.MissionCompletionRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GamificationService {
    private static final Logger logger = LoggerFactory.getLogger(GamificationService.class);

    // Constantes de XP distribuídos por ação do user
    private static final long XP_PER_WORKOUT = 150;
    private static final double XP_PER_VOLUME_UNIT = 0.5;
    private static final double VOLUME_XP_CAP_RATIO = 2.0;

    private final WorkoutLogRepository workoutLogRepository;
    private final MissionCompletionRepository missionCompletionRepository;
    private final List<AchievementChecker> achievementCheckers;
    private final List<MissionChecker> missionCheckers;
    private final UserService userService;

    @Autowired
    public GamificationService(WorkoutLogRepository workoutLogRepository, MissionCompletionRepository missionCompletionRepository, List<AchievementChecker> achievementCheckers, List<MissionChecker> missionCheckers, UserService userService) {
        this.workoutLogRepository = workoutLogRepository;
        this.missionCompletionRepository = missionCompletionRepository;
        this.achievementCheckers = achievementCheckers;
        this.missionCheckers = missionCheckers;
        this.userService = userService;
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
                        totalMissionXp
                    );
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

    public void awardXpForWorkout(User user, WorkoutLog workoutLog) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();

        long workoutCompletedTodayCount = workoutLogRepository.countByUserAndStatusAndCompletedAtAfter(user, WorkoutLogStatus.COMPLETED, startOfDay);

        double dailyXpModifier = 0;
        switch ((int) workoutCompletedTodayCount) {
            case 1: // Caso seja o primeiro treino do dia
                dailyXpModifier = 1.0;
                break;
            case 2: // Caso seja o segundo treino do dia
                dailyXpModifier = 0.5;
                break;
            default: // Caso seja o terceiro ou mais
                dailyXpModifier = 0.1;
                break;
        }

        long actualVolumeXp = 0;
        if (workoutLog.getTotalVolume() != null && workoutLog.getTotalVolume() > 0) {
            actualVolumeXp = (long) (workoutLog.getTotalVolume() * XP_PER_VOLUME_UNIT);
        }

        // Cálculo do teto do XP proveniente do volume
        long volumeXpCap = (long) (XP_PER_WORKOUT * VOLUME_XP_CAP_RATIO);
        // O XP final proveniente do volume é o MENOR valor entre o que o usuário fez e o teto
        long finalVolumeXp = Math.min(actualVolumeXp, volumeXpCap);

        long totalBaseXp = XP_PER_WORKOUT + finalVolumeXp;

        long xpGained = (long) (totalBaseXp * dailyXpModifier);

        logger.info(
                "---------------------------------------------------------" +
                        "Concedendo {} XP para o usuário {} (Base: {}, Volume: {}/{} capped, Modificador Diário: {}%) " +
                        "----------------------------------------------------------------",
                xpGained,
                user.getEmail(),
                XP_PER_WORKOUT,
                finalVolumeXp,
                actualVolumeXp,
                (int)(dailyXpModifier * 100)
        );
        user.setExperiencePoints(user.getExperiencePoints() + xpGained);

        checkLevelUp(user);
    }

    // Cálculo de XP necessário para próximo nível, resultado exponencial conforme o nível
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
