package com.musclebuilder.controller;

import com.musclebuilder.dto.WorkoutDTO;
import com.musclebuilder.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/workouts")
@CrossOrigin(origins = "http://localhost:4200")
public class WorkoutController {

    private final WorkoutService workoutService;

    @Autowired
    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public ResponseEntity<WorkoutDTO> createWorkout(@Valid @RequestBody WorkoutDTO workoutDTO, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        WorkoutDTO createdWorkout = workoutService.createWorkout(workoutDTO, userId);
        return new ResponseEntity<>(createdWorkout, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutDTO> getWorkoutById(@PathVariable Long id, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        WorkoutDTO workout = workoutService.getWorkoutById(id, userId);
        return ResponseEntity.ok(workout);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutDTO>> getUserWorkouts(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        List<WorkoutDTO> workouts = workoutService.getUserWorkouts(userId);
        return ResponseEntity.ok(workouts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutDTO> updateWorkout(@PathVariable Long id, @Valid @RequestBody WorkoutDTO workoutDTO, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        WorkoutDTO updatedWorkout = workoutService.updateWorkout(id, workoutDTO, userId);
        return ResponseEntity.ok(updatedWorkout);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        workoutService.deleteWorkout(id, userId);
        return ResponseEntity.noContent().build();
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        String username = principal.getName();
        return workoutService.getUserIdByEmail(username);
    }

}
