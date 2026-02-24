package com.musclebuilder.controller;

import com.musclebuilder.dto.ExerciseDTO;
import com.musclebuilder.model.MuscleGroup;
import com.musclebuilder.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@CrossOrigin(origins = "http://localhost:4200")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Autowired
    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping
    public ResponseEntity<ExerciseDTO> createExercise(@Valid @RequestBody ExerciseDTO exerciseDTO) {
        ExerciseDTO savedExercise = exerciseService.createExercise(exerciseDTO);
        return new ResponseEntity<>(savedExercise, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDTO> getExerciseById(@PathVariable Long id) {
        ExerciseDTO exercise = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(exercise);
    }

    @GetMapping
    public ResponseEntity<List<ExerciseDTO>> getAllExercises() {
        List<ExerciseDTO> exercises = exerciseService.getAllExercises();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/muscle-group/{muscleGroup}")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByMuscleGroup(@PathVariable MuscleGroup muscleGroup) {
        List<ExerciseDTO> exercises = exerciseService.getExerciseByMuscleGroup(muscleGroup);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ExerciseDTO>> searchExercisesByName(@RequestParam String name) {
        List<ExerciseDTO> exercises = exerciseService.searchExercisesByName(name);
        return ResponseEntity.ok(exercises);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseDTO> updateExercise(@PathVariable Long id, @Valid @RequestBody ExerciseDTO exerciseDTO) {
        ExerciseDTO updatedExercise = exerciseService.updateExercise(id, exerciseDTO);
        return ResponseEntity.ok(updatedExercise);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }

}
