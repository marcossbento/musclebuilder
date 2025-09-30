package com.musclebuilder.service;

import com.musclebuilder.dto.AchievementDTO;
import com.musclebuilder.dto.DashboardDTO;
import com.musclebuilder.dto.MissionProgressDTO;
import com.musclebuilder.model.User;
import com.musclebuilder.repository.AchievementRepository;
import com.musclebuilder.service.security.SecurityContextService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {

    private final GamificationService gamificationService;
    private final ProgressService progressService;
    private final AchievementRepository achievementRepository;
    private final List<MissionChecker> missionCheckers;
    private final RecommendationService recommendationService;
    private final SecurityContextService securityContextService;

    public DashboardService(UserService userService,
                            GamificationService gamificationService,
                            ProgressService progressService,
                            AchievementRepository achievementRepository,
                            List<MissionChecker> missionCheckers,
                            RecommendationService recommendationService,
                            SecurityContextService securityContextService
    ) {
        this.gamificationService = gamificationService;
        this.progressService = progressService;
        this.achievementRepository = achievementRepository;
        this.missionCheckers = missionCheckers;
        this.recommendationService = recommendationService;
        this.securityContextService = securityContextService;
    }

    public DashboardDTO getDashboardData() {
        User currentUser = securityContextService.findCurrentUser();

        DashboardDTO.UserLevelDTO userLevel = buildUserLevelDTO(currentUser);
        DashboardDTO.GamificationStatsDTO stats = buildStatsDTO(currentUser);

        List<MissionProgressDTO> activeMissions = buildWeeklyMissionList(currentUser);

        Optional<DashboardDTO.RecommendedWorkoutDTO> recommendedWorkout = findRecommendedWorkout(currentUser);
        Optional<AchievementDTO> lastAchievement = findLastAchievement(currentUser);

        return new DashboardDTO(
                userLevel,
                stats,
                activeMissions,
                recommendedWorkout,
                lastAchievement
        );
    }

    private DashboardDTO.UserLevelDTO buildUserLevelDTO(User user) {
        long xpForCurrentLevel = gamificationService.getTotalXpForLevel(user.getLevel());
        long xpForNextLevel = gamificationService.getTotalXpForLevel(user.getLevel() + 1);

        // Tamanho da barra XP = diferença entre o próximo nível e o atual.
        long xpNeededInThisLevel = xpForNextLevel - xpForCurrentLevel;

        long userProgressInThisLevel = user.getExperiencePoints() - xpForCurrentLevel;

        double progressPercentage = 0;
        if (xpNeededInThisLevel > 0) {
            progressPercentage = ((double) userProgressInThisLevel / xpNeededInThisLevel) * 100;
        }

        return new DashboardDTO.UserLevelDTO(
                user.getLevel(),
                userProgressInThisLevel,
                xpNeededInThisLevel,
                progressPercentage
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

    private List<MissionProgressDTO> buildWeeklyMissionList(User user) {
        return missionCheckers.stream()
                .map(checker -> new MissionProgressDTO(
                        checker.getDescription(),
                        checker.getXpReward(),
                        checker.getGoal(),
                        checker.getCurrentProgress(user)
                ))
                .toList();
    }

    private Optional<DashboardDTO.RecommendedWorkoutDTO> findRecommendedWorkout(User user) {
        return recommendationService.recommendWorkout(user)
                .map(workout -> {
                    return new DashboardDTO.RecommendedWorkoutDTO(
                            workout.getId(),
                            workout.getName(),
                            workout.getDescription()
                    );
                });
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
