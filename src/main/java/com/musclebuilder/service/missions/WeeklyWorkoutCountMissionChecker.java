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
        LocalDate todayDate = LocalDate.now();
        LocalDate mondayDate = todayDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDateTime startOfWeek = mondayDate.atStartOfDay();
        User currentUser = event.getWorkoutLog().getUser();

        boolean isRewardAlreadyClaimedThisWeek = missionCompletionRepository.existsByUserAndMissionIdAndCompletedAtAfter(currentUser, MISSION_ID, startOfWeek);

        if (isRewardAlreadyClaimedThisWeek) {
            return Optional.empty();
        }

        long workoutsCount = workoutLogRepository.countByUserAndStatusAndCompletedAtAfter(currentUser, WorkoutLogStatus.COMPLETED, startOfWeek);

        if (workoutsCount >= MISSION_GOAL) {
            return Optional.of(XP_REWARD);
        }

        return Optional.empty();

    }

    @Override
    public String getMissionId() {
        return MISSION_ID;
    }
}