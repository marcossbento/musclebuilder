package com.musclebuilder.controller;

import com.musclebuilder.dto.LogExerciseRequest;
import com.musclebuilder.dto.StartWorkoutRequest;
import com.musclebuilder.dto.WorkoutLogResponse;
import com.musclebuilder.service.UserService;
import com.musclebuilder.service.WorkoutLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/workout-logs")
public class WorkoutLogController {

    private final WorkoutLogService workoutLogService;

    private final UserService userService;

    @Autowired
    public WorkoutLogController(WorkoutLogService workoutLogService, UserService userService) {
        this.workoutLogService = workoutLogService;
        this.userService = userService;
    }

    @PostMapping("/start")
    public ResponseEntity<WorkoutLogResponse> startWorkout(@Valid @RequestBody StartWorkoutRequest request, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        WorkoutLogResponse response = workoutLogService.startWorkout(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //Adiciona um exercício a um registro de treino existente
    @PostMapping("/{logId}/exercises")
    public ResponseEntity<WorkoutLogResponse> logExercise(@PathVariable Long logId, @Valid @RequestBody LogExerciseRequest request, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        WorkoutLogResponse response = workoutLogService.logExercise(logId, userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{logId}/complete")
    public ResponseEntity<WorkoutLogResponse> completeWorkout(@PathVariable Long logId, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        WorkoutLogResponse response = workoutLogService.completeWorkout(logId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{logId}")
    public ResponseEntity<WorkoutLogResponse> getWorkoutLog(@PathVariable Long logId, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        WorkoutLogResponse response = workoutLogService.getWorkoutLog(logId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutLogResponse>> getAllUserWorkoutLogs(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<WorkoutLogResponse> response = workoutLogService.getAllUserWorkoutLogs(userId);
        return ResponseEntity.ok(response);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        String userEmail = authentication.getName();

        return userService.getUserByEmail(userEmail).id();
    }

}
