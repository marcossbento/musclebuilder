package com.musclebuilder.service;

import com.musclebuilder.dto.WorkoutCreateDTO;
import com.musclebuilder.dto.WorkoutResponseDTO;
import com.musclebuilder.dto.WorkoutUpdateDTO;
import com.musclebuilder.exception.ResourceNotFoundException;
import com.musclebuilder.mapper.WorkoutMapper;
import com.musclebuilder.model.*;
import com.musclebuilder.repository.ExerciseRepository;
import com.musclebuilder.repository.UserRepository;
import com.musclebuilder.repository.WorkoutRepository;
import com.musclebuilder.service.security.SecurityContextService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutService {

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
                    .orElseThrow(() -> new EntityNotFoundException("Exercício com ID " + exerciseDto.exerciseId() + " não encontrado"));

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
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkoutResponseDTO findWorkoutsByIdForCurrentUser(Long workoutId) {
        User currentUser = securityContextService.findCurrentUser();
        Workout workout = workoutRepository.findByIdAndUserWithExercises(workoutId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Treino com ID " + workoutId + " não encontrado"));
        return workoutMapper.toDto(workout);
    }

    @Transactional
    public void deleteWorkout(Long workoutId) {
        User currentUser = securityContextService.findCurrentUser();
        Workout workout = workoutRepository.findByIdAndUser(workoutId, currentUser)
                        .orElseThrow(() -> new ResourceNotFoundException("Treino com ID " + workoutId + "não encontrado para este usuário"));

        workoutRepository.delete(workout);
    }

    @Transactional
    public WorkoutResponseDTO updateWorkout(Long workoutId, WorkoutUpdateDTO dto) {
        User currentUser = securityContextService.findCurrentUser();

        Workout workoutToUpdate = workoutRepository.findByIdAndUser(workoutId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Treino com ID " + workoutId + " não encontrado para este usuário"));

        workoutToUpdate.setName(dto.name());
        workoutToUpdate.setDescription(dto.description());
        workoutToUpdate.setDifficultyLevel(dto.difficultyLevel());

        workoutToUpdate.getWorkoutExercises().clear();

        dto.exercises().forEach(exerciseDto -> {
            Exercise exercise = exerciseRepository.findById(exerciseDto.exerciseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Exercício com ID " + exerciseDto.exerciseId() + " não encontrado."));

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
