package com.musclebuilder.controller;

import com.musclebuilder.dto.CompleteWorkoutResponseDTO;
import com.musclebuilder.dto.LogExerciseRequest;
import com.musclebuilder.dto.StartWorkoutRequest;
import com.musclebuilder.dto.WorkoutLogResponseDTO;
import com.musclebuilder.service.UserService;
import com.musclebuilder.service.WorkoutLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/workout-logs")
public class WorkoutLogController {

    private final WorkoutLogService workoutLogService;

    @Autowired
    public WorkoutLogController(WorkoutLogService workoutLogService, UserService userService) {
        this.workoutLogService = workoutLogService;
    }

    @PostMapping("/start")
    public ResponseEntity<WorkoutLogResponseDTO> startWorkout(@Valid @RequestBody StartWorkoutRequest request) {
        WorkoutLogResponseDTO response = workoutLogService.startWorkout(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //Adiciona um exercício a um registro de treino existente
    @PostMapping("/{logId}/exercises")
    public ResponseEntity<WorkoutLogResponseDTO> logExercise(@PathVariable Long logId, @Valid @RequestBody LogExerciseRequest request) {
        WorkoutLogResponseDTO response = workoutLogService.logExercise(logId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{logId}/complete")
    public ResponseEntity<CompleteWorkoutResponseDTO> completeWorkout(@PathVariable Long logId) {
        CompleteWorkoutResponseDTO response = workoutLogService.completeWorkout(logId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{logId}")
    public ResponseEntity<WorkoutLogResponseDTO> getWorkoutLog(@PathVariable Long logId) {
        WorkoutLogResponseDTO response = workoutLogService.getWorkoutLog(logId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutLogResponseDTO>> getAllUserWorkoutLogs() {
        List<WorkoutLogResponseDTO> response = workoutLogService.getAllUserWorkoutLogs();
        return ResponseEntity.ok(response);
    }
}
