package com.musclebuilder.controller;

import com.musclebuilder.dto.WorkoutResponseDTO;
import com.musclebuilder.mapper.WorkoutMapper;
import com.musclebuilder.model.User;
import com.musclebuilder.model.Workout;
import com.musclebuilder.model.WorkoutExercise;
import com.musclebuilder.service.RecommendationService;
import com.musclebuilder.service.security.SecurityContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

@RestController
@RequestMapping("/api/workouts")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final SecurityContextService securityContextService;
    private final WorkoutMapper workoutMapper;

    @Autowired
    public RecommendationController(RecommendationService recommendationService, SecurityContextService securityContextService, WorkoutMapper workoutMapper) {
        this.recommendationService = recommendationService;
        this.securityContextService = securityContextService;
        this.workoutMapper = workoutMapper;
    }

    @GetMapping("/recommended")
    public ResponseEntity<WorkoutResponseDTO> getRecommendedWorkout() {
        User currentUser = securityContextService.findCurrentUser();

        Optional<Workout> recommendedWorkoutOpt = recommendationService.recommendWorkout(currentUser);

        return recommendedWorkoutOpt
                .map(workout -> {
                    WorkoutResponseDTO responseDTO = workoutMapper.toDto(workout);
                    return ResponseEntity.ok(responseDTO);
                })
                .orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });
    }
}
