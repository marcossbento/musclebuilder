package com.musclebuilder.controller;

import com.musclebuilder.dto.WorkoutResponseDTO;
import com.musclebuilder.model.User;
import com.musclebuilder.model.Workout;
import com.musclebuilder.model.WorkoutExercise;
import com.musclebuilder.service.RecommendationService;
import com.musclebuilder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workouts")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserService userService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService, UserService userService) {
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    @GetMapping("/recommended")
    public ResponseEntity<WorkoutResponseDTO> getRecommendedWorkout() {
        User currentUser = userService.findCurrentUser();

        Optional<Workout> recommendedWorkoutOpt = recommendationService.recommendWorkout(currentUser);

        return recommendedWorkoutOpt
                .map(workout -> {
                    WorkoutResponseDTO responseDTO = mapToWorkoutResponseDTO(workout);
                    return ResponseEntity.ok(responseDTO);
                })
                .orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });
    }

    private WorkoutResponseDTO mapToWorkoutResponseDTO(Workout workout) {
        List<WorkoutResponseDTO.WorkoutExerciseDTO> exerciseDTOs = workout.getWorkoutExercises().stream()
                .sorted(Comparator.comparingInt(WorkoutExercise::getOrderPosition))
                .map(we -> new WorkoutResponseDTO.WorkoutExerciseDTO(
                        we.getId(),
                        we.getExercise().getId(),
                        we.getExercise().getName(),
                        we.getSets(),
                        we.getRepsPerSet(),
                        we.getWeight(),
                        we.getRestSeconds(),
                        we.getOrderPosition()
                ))
                .collect(Collectors.toList());

        return new WorkoutResponseDTO(
                workout.getId(),
                workout.getName(),
                workout.getDescription(),
                workout.getWorkoutType(),
                workout.getUser().getId(),
                workout.getWeekNumber(),
                workout.getDayNumber(),
                workout.getStatus(),
                workout.getEstimatedDurationMinutes(),
                workout.getDifficultyLevel(),
                exerciseDTOs,
                workout.getCreatedAt(),
                workout.getUpdatedAt()
        );
    }

}
