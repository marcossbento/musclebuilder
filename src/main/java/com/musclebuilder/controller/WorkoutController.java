package com.musclebuilder.controller;

import com.musclebuilder.dto.WorkoutCreateDTO;
import com.musclebuilder.dto.WorkoutResponseDTO;
import com.musclebuilder.dto.WorkoutUpdateDTO;
import com.musclebuilder.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    @Autowired
    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public ResponseEntity<WorkoutResponseDTO> createWorkout(@Valid @RequestBody WorkoutCreateDTO workoutCreateDTO) {
        WorkoutResponseDTO createdWorkout = workoutService.createWorkout(workoutCreateDTO);
        return new ResponseEntity<>(createdWorkout, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutResponseDTO>> getUserWorkouts() {
        List<WorkoutResponseDTO> workouts = workoutService.findWorkoutsForCurrentUser();
        return ResponseEntity.ok(workouts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponseDTO> getWorkoutById(@PathVariable Long id) {
        WorkoutResponseDTO workout = workoutService.findWorkoutsByIdForCurrentUser(id);
        return ResponseEntity.ok(workout);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponseDTO> updateWorkout(@PathVariable Long id, @Valid @RequestBody WorkoutUpdateDTO workoutUpdateDTO) {
        WorkoutResponseDTO updatedWorkout = workoutService.updateWorkout(id, workoutUpdateDTO);
        return ResponseEntity.ok(updatedWorkout);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }
}
