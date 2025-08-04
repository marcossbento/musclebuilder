package com.musclebuilder.service;

import com.musclebuilder.dto.WorkoutCreateDTO;
import com.musclebuilder.dto.WorkoutResponseDTO;
import com.musclebuilder.dto.WorkoutUpdateDTO;
import com.musclebuilder.exception.ResourceNotFoundException;
import com.musclebuilder.model.*;
import com.musclebuilder.repository.ExerciseRepository;
import com.musclebuilder.repository.UserRepository;
import com.musclebuilder.repository.WorkoutRepository;
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
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    @Autowired
    public WorkoutService(WorkoutRepository workoutRepository, UserRepository userRepository, ExerciseRepository exerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional
    public WorkoutResponseDTO createWorkout(WorkoutCreateDTO dto) {
        User currentUser = findCurrentUser();

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
        return mapToResponseDTO(savedWorkout);
    }

    @Transactional(readOnly = true)
    public List<WorkoutResponseDTO> findWorkoutsForCurrentUser() {
        User currentUser = findCurrentUser();
        return workoutRepository.findByUserOrderByNameAsc(currentUser).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkoutResponseDTO findWorkoutsByIdForCurrentUser(Long workoutId) {
        User currentUser = findCurrentUser();
        Workout workout = workoutRepository.findByIdAndUser(workoutId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Treino com ID " + workoutId + " não encontrado"));
        return mapToResponseDTO(workout);
    }

    @Transactional
    public void deleteWorkout(Long workoutId) {
        User currentUser = findCurrentUser();
        Workout workout = workoutRepository.findByIdAndUser(workoutId, currentUser)
                        .orElseThrow(() -> new ResourceNotFoundException("Treino com ID " + workoutId + "não encontrado para este usuário"));

        workoutRepository.delete(workout);
    }

    @Transactional
    public WorkoutResponseDTO updateWorkout(Long workoutId, WorkoutUpdateDTO dto) {
        User currentUser = findCurrentUser();

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

        return mapToResponseDTO(updatedWorkout);
    }

    // MÉTODOS AUXILIARES

    private User findCurrentUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado: " + userEmail));
    }

    private WorkoutResponseDTO mapToResponseDTO(Workout workout) {
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
