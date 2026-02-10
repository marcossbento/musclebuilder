package com.musclebuilder.service;

import com.musclebuilder.dto.WorkoutCreateDTO;
import com.musclebuilder.dto.WorkoutResponseDTO;
import com.musclebuilder.dto.WorkoutUpdateDTO;
import com.musclebuilder.exception.ResourceNotFoundException;
import com.musclebuilder.mapper.WorkoutMapper;
import com.musclebuilder.model.*;
import com.musclebuilder.repository.ExerciseRepository;
import com.musclebuilder.repository.WorkoutRepository;
import com.musclebuilder.service.security.SecurityContextService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkoutService {

    private static final String WORKOUT_NOT_FOUND_MESSAGE = "Treino não encontrado com ID: ";
    private static final String EXERCISE_NOT_FOUND_MESSAGE = "Exercício não encontrado com ID: ";

    private final WorkoutRepository workoutRepository;
    private final SecurityContextService securityContextService;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutMapper workoutMapper;

    @Autowired
    public WorkoutService(WorkoutRepository workoutRepository, SecurityContextService securityContextService, ExerciseRepository exerciseRepository, WorkoutMapper workoutMapper) {
        this.workoutRepository = workoutRepository;
        this.securityContextService = securityContextService;
        this.exerciseRepository = exerciseRepository;
        this.workoutMapper = workoutMapper;
    }

    @Transactional
    public WorkoutResponseDTO createWorkout(WorkoutCreateDTO dto) {
        User currentUser = securityContextService.findCurrentUser();

        Workout newWorkout = new Workout(
                dto.name(),
                dto.description(),
                currentUser,
                dto.difficultyLevel()
        );

        dto.exercises().forEach(exerciseDto -> {
            Exercise exercise = exerciseRepository.findById(exerciseDto.exerciseId())
                    .orElseThrow(() -> new EntityNotFoundException(EXERCISE_NOT_FOUND_MESSAGE + exerciseDto.exerciseId()));

            newWorkout.addExercise(
                    exercise,
                    exerciseDto.sets(),
                    exerciseDto.repsPerSet(),
                    exerciseDto.weight(),
                    exerciseDto.restSeconds(),
                    0
            );
        });

        Workout savedWorkout = workoutRepository.save(newWorkout);
        return workoutMapper.toDto(savedWorkout);
    }

    @Transactional(readOnly = true)
    public List<WorkoutResponseDTO> findWorkoutsForCurrentUser() {
        User currentUser = securityContextService.findCurrentUser();
        return workoutRepository.findByUserOrderByNameAsc(currentUser).stream()
                .map(workoutMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkoutResponseDTO findWorkoutsByIdForCurrentUser(Long workoutId) {
        User currentUser = securityContextService.findCurrentUser();
        Workout workout = workoutRepository.findByIdAndUserWithExercises(workoutId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(WORKOUT_NOT_FOUND_MESSAGE+ workoutId));
        return workoutMapper.toDto(workout);
    }

    @Transactional
    public void deleteWorkout(Long workoutId) {
        User currentUser = securityContextService.findCurrentUser();
        Workout workout = workoutRepository.findByIdAndUser(workoutId, currentUser)
                        .orElseThrow(() -> new ResourceNotFoundException(WORKOUT_NOT_FOUND_MESSAGE + workoutId));

        workoutRepository.delete(workout);
    }

    @Transactional
    public WorkoutResponseDTO updateWorkout(Long workoutId, WorkoutUpdateDTO dto) {
        User currentUser = securityContextService.findCurrentUser();

        Workout workoutToUpdate = workoutRepository.findByIdAndUser(workoutId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(WORKOUT_NOT_FOUND_MESSAGE + workoutId));

        workoutToUpdate.setName(dto.name());
        workoutToUpdate.setDescription(dto.description());
        workoutToUpdate.setDifficultyLevel(dto.difficultyLevel());

        workoutToUpdate.getWorkoutExercises().clear();

        dto.exercises().forEach(exerciseDto -> {
            Exercise exercise = exerciseRepository.findById(exerciseDto.exerciseId())
                    .orElseThrow(() -> new ResourceNotFoundException(EXERCISE_NOT_FOUND_MESSAGE + exerciseDto.exerciseId()));

            workoutToUpdate.addExercise(
                    exercise,
                    exerciseDto.sets(),
                    exerciseDto.repsPerSet(),
                    exerciseDto.weight(),
                    exerciseDto.restSeconds(),
                    0
            );
        });

        Workout updatedWorkout = workoutRepository.save(workoutToUpdate);

        return workoutMapper.toDto(updatedWorkout);
    }
}
