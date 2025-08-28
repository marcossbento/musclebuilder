package com.musclebuilder.service;

import com.musclebuilder.dto.AchievementDTO;
import com.musclebuilder.dto.DashboardDTO;
import com.musclebuilder.model.User;
import com.musclebuilder.model.WorkoutLogStatus;
import com.musclebuilder.repository.AchievementRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Service
public class DashboardService {

    private final UserService userService;
    private final GamificationService gamificationService;
    private final ProgressService progressService;
    private final AchievementRepository achievementRepository;
    private final WorkoutLogRepository workoutLogRepository;

    public DashboardService(UserService userService, GamificationService gamificationService, ProgressService progressService, AchievementRepository achievementRepository, WorkoutLogRepository workoutLogRepository) {
        this.userService = userService;
        this.gamificationService = gamificationService;
        this.progressService = progressService;
        this.achievementRepository = achievementRepository;
        this.workoutLogRepository = workoutLogRepository;
    }

    public DashboardDTO getDashboardData() {
        User currentUser = userService.findCurrentUser();

        DashboardDTO.UserLevelDTO userLevel = buildUserLevelDTO(currentUser);
        DashboardDTO.GamificationStatsDTO stats = buildStatsDTO(currentUser);
        DashboardDTO.WeeklyMissionDTO weeklyMission = buildWeeklyMissionDTO(currentUser);
        Optional<DashboardDTO.RecommendedWorkoutDTO> recommendedWorkout = findRecommendedWorkout(currentUser);
        Optional<AchievementDTO> lastAchievement = findLastAchievement(currentUser);

        return new DashboardDTO(
                userLevel,
                stats,
                weeklyMission,
                recommendedWorkout,
                lastAchievement
        );
    }

    private DashboardDTO.UserLevelDTO buildUserLevelDTO(User user) {
        long xpForNext = gamificationService.getXpForLevel(user.getLevel() + 1);
        double progress = (double) user.getExperiencePoints() / xpForNext * 100;

        return new DashboardDTO.UserLevelDTO(
                user.getLevel(),
                user.getExperiencePoints(),
                xpForNext,
                progress
        );
    }

    private DashboardDTO.GamificationStatsDTO buildStatsDTO(User user) {
        var summary = progressService.getSummaryForCurrentUser();
        int streak = gamificationService.calculateWorkoutStreak(user);

        return new DashboardDTO.GamificationStatsDTO(
                summary.totalWorkouts(),
                summary.totalVolume(),
                streak
        );
    }

    private DashboardDTO.WeeklyMissionDTO buildWeeklyMissionDTO(User user) {
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        int weeklyGoal = 3;
        long workoutsThisWeek = workoutLogRepository.countByUserAndStatusAndStartedAtAfter(
                user,
                WorkoutLogStatus.COMPLETED,
                startOfWeek.atStartOfDay()
        );

        return new DashboardDTO.WeeklyMissionDTO(
                "Complete " + weeklyGoal + " treinos essa semana",
                (int) workoutsThisWeek,
                weeklyGoal
        );
    }

    private Optional<DashboardDTO.RecommendedWorkoutDTO> findRecommendedWorkout(User user) {
        return workoutLogRepository.findFirstByUserAndStatusOrderByCompletedAtDesc(user, WorkoutLogStatus.COMPLETED)
                .map(lastLog -> new DashboardDTO.RecommendedWorkoutDTO(
                        lastLog.getWorkout().getId(),
                        lastLog.getWorkoutName(),
                        lastLog.getWorkout().getDescription()
                ));
    }

    private Optional<AchievementDTO> findLastAchievement(User user) {
        return achievementRepository.findFirstByUserOrderByEarnedAtDesc(user)
                .map(achieve -> new AchievementDTO(
                        achieve.getName(),
                        achieve.getDescription(),
                        achieve.getBadgeUrl(),
                        achieve.getEarnedAt()
                ));
    }
}
