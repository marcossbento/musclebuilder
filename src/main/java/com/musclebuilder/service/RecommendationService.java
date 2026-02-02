package com.musclebuilder.service;

import com.musclebuilder.model.*;
import com.musclebuilder.repository.WorkoutLogRepository;
import com.musclebuilder.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final WorkoutLogRepository workoutLogRepository;
    private final WorkoutRepository workoutRepository;
    private final Random random = new Random();

    public RecommendationService(WorkoutLogRepository workoutLogRepository, WorkoutRepository workoutRepository) {
        this.workoutLogRepository = workoutLogRepository;
        this.workoutRepository = workoutRepository;
    }

    public Optional<Workout> recommendWorkout(User user) {
        Optional<WorkoutLog> lastWorkoutLogOpt = workoutLogRepository
                .findFirstByUserAndStatusOrderByCompletedAtDesc(user, WorkoutLogStatus.COMPLETED);

        if (lastWorkoutLogOpt.isEmpty()) {
            return recommendForNewUser();
        }

        WorkoutLog lastLog = lastWorkoutLogOpt.get();
        if (lastLog.getWorkout() == null || lastLog.getWorkout().getWorkoutType() == null) {
            return recommendForNewUser();
        }

        WorkoutType lastWorkoutType = lastLog.getWorkout().getWorkoutType();

        List<WorkoutType> recommendedTypes = getNextRecommendedTypes(lastWorkoutType);

        List<Workout> candidateWorkouts = recommendedTypes
                .stream()
                .flatMap(type -> workoutRepository.findByWorkoutType(type).stream())
                .toList();

        if (!candidateWorkouts.isEmpty()) {
            Workout recommendedWorkout = candidateWorkouts.get(random.nextInt(candidateWorkouts.size()));
            return Optional.of(recommendedWorkout);
        }

        return recommendForNewUser();
    }

    private Optional<Workout> recommendForNewUser() {
        List<Workout> fullBodyWorkouts = workoutRepository.findByWorkoutType(WorkoutType.FULL_BODY);

        if (!fullBodyWorkouts.isEmpty()) {
            return Optional.of(fullBodyWorkouts.get(random.nextInt(fullBodyWorkouts.size())));
        }

        return Optional.empty();
    }

    private List<WorkoutType> getNextRecommendedTypes(WorkoutType lastWorkoutType) {
        switch (lastWorkoutType) {
            case PUSH:
                return List.of(WorkoutType.PULL, WorkoutType.LEGS);
            case PULL:
                return List.of(WorkoutType.PUSH, WorkoutType.LEGS);
            case LEGS:
                return List.of(WorkoutType.PUSH, WorkoutType.PULL);
            case FULL_BODY:
            default:
                return List.of(WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS);
        }
    }

}
