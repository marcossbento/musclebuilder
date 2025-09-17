package com.musclebuilder.service.missions;

import com.musclebuilder.event.WorkoutCompletedEvent;
import com.musclebuilder.model.User;
import com.musclebuilder.model.WorkoutLogStatus;
import com.musclebuilder.repository.MissionCompletionRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import com.musclebuilder.service.MissionChecker;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Component
public class WeeklyWorkoutCountMissionChecker implements MissionChecker {

    private static final String MISSION_ID = "WEEKLY_3_WORKOUTS";
    private static final String DESCRIPTION = "Complete 3 treinos essa semana";
    private static final Long XP_REWARD = 250L;
    private static final int MISSION_GOAL = 3;

    private final WorkoutLogRepository workoutLogRepository;
    private final MissionCompletionRepository missionCompletionRepository;

    public WeeklyWorkoutCountMissionChecker(WorkoutLogRepository workoutLogRepository, MissionCompletionRepository missionCompletionRepository) {
        this.workoutLogRepository = workoutLogRepository;
        this.missionCompletionRepository = missionCompletionRepository;
    }


    @Override
    public Optional<Long> check(WorkoutCompletedEvent event) {
        User currentUser = event.getWorkoutLog().getUser();

        LocalDateTime startOfWeek = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();

        boolean isRewardAlreadyClaimedThisWeek = missionCompletionRepository.existsByUserAndMissionIdAndCompletedAtAfter(currentUser, MISSION_ID, startOfWeek);

        if (isRewardAlreadyClaimedThisWeek) {
            return Optional.empty();
        }

        long currentProgress = getCurrentProgress(currentUser);

        if (currentProgress >= MISSION_GOAL) {
            return Optional.of(XP_REWARD);
        }

        return Optional.empty();
    }

    @Override
    public String getMissionId() {
        return MISSION_ID;
    }

    @Override
    public String getDescription() { return DESCRIPTION; }

    @Override
    public long getXpReward() { return XP_REWARD; }

    @Override
    public long getGoal() { return MISSION_GOAL; }

    @Override
    public long getCurrentProgress(User user) {
        LocalDateTime startOfWeek = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();

        return workoutLogRepository.
                countByUserAndStatusAndCompletedAtAfter(user, WorkoutLogStatus.COMPLETED, startOfWeek);
    }

}