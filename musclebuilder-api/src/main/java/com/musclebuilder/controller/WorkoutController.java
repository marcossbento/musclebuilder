package com.musclebuilder.controller;

import com.musclebuilder.dto.WorkoutDTO;
import com.musclebuilder.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/workouts")
@CrossOrigin(origins = "*")
public class WorkoutController {

    private final WorkoutService workoutService;

    @Autowired
    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public ResponseEntity<WorkoutDTO> createWorkout(@Valid @RequestBody WorkoutDTO workoutDTO, @AuthenticationPrincipal UserPrincipal currentUser) {
        WorkoutDTO createdWorkout = workoutService.createWorkout(workoutDTO, currentUser.getId());
        return new ResponseEntity<>(createdWorkout, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutDTO> getWorkoutById(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        WorkoutDTO workout = workoutService.getWorkoutById(id, currentUser.getId());
        return ResponseEntity.ok(workout);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutDTO>> getUserWorkouts(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<WorkoutDTO> workouts = workoutService.getUserWorkouts(currentUser.getId());
        return ResponseEntity.ok(workouts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutDTO> updateWorkout(@PathVariable Long id, @Valid @RequestBody WorkoutDTO workoutDTO, @AuthenticationPrincipal UserPrincipal currentUser) {
        WorkoutDTO updatedWorkout = workoutService.updateWorkout(id, workoutDTO, currentUser.getId());
        return ResponseEntity.ok(updatedWorkout);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        workoutService.deleteWorkout(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

}
