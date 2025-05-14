package com.musclebuilder.service;

import com.musclebuilder.dto.WorkoutDTO;
import com.musclebuilder.exception.ResourceNotFoundException;
import com.musclebuilder.exception.UnauthorizedAccessException;
import com.musclebuilder.model.*;
import com.musclebuilder.repository.ExerciseRepository;
import com.musclebuilder.repository.UserRepository;
import com.musclebuilder.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public WorkoutDTO createWorkout(WorkoutDTO workoutDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Workout workout = convertToEntity(workoutDTO);
        workout.setUser(user);

        if (workoutDTO.getExercises() != null) {
            workoutDTO.getExercises().forEach(exerciseDTO -> {
                Exercise exercise = exerciseRepository.findById(exerciseDTO.getExerciseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado"));

                workout.addExercise(
                        exercise,
                        exerciseDTO.getSets(),
                        exerciseDTO.getRepsPerSet(),
                        exerciseDTO.getWeight(),
                        exerciseDTO.getRestSeconds(),
                        exerciseDTO.getOrderPosition()
                );
            });
        }

        Workout savedWorkout = workoutRepository.save(workout);
        return convertToDTO(savedWorkout);
    }

    public WorkoutDTO getWorkoutById(Long id, Long userId) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado"));

        if (!workout.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Você não tem permissão para acessar esse treino");
        }

        return convertToDTO(workout);
    }

    public List<WorkoutDTO> getUserWorkouts(Long userId) {
        List<Workout> workouts = workoutRepository.findByUserId(userId);

        return workouts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public WorkoutDTO updateWorkout(Long id, WorkoutDTO workoutDTO, Long userId) {
        // Verifica se o workout existe e pertence ao usuário
        if (!workoutRepository.existsByIdAndUserId(id, userId)) {
            throw new UnauthorizedAccessException("Você não tem permissão para modificar este treino");
        }

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado"));

        workout.setName(workoutDTO.getName());
        workout.setDescription(workoutDTO.getDescription());
        workout.setWorkoutType(workoutDTO.getWorkoutType());
        workout.setWeekNumber(workoutDTO.getWeekNumber());
        workout.setDayNumber(workoutDTO.getDayNumber());
        workout.setEstimatedDurationMinutes(workoutDTO.getEstimatedDurationMinutes());

        //Limpa e adiciona exercícios novamente
        workout.getWorkoutExercises().clear();

        if (workoutDTO.getExercises() != null) {
            workoutDTO.getExercises().forEach(exerciseDTO -> {
                Exercise exercise = exerciseRepository.findById(exerciseDTO.getExerciseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado"));

                workout.addExercise(
                        exercise,
                        exerciseDTO.getSets(),
                        exerciseDTO.getRepsPerSet(),
                        exerciseDTO.getWeight(),
                        exerciseDTO.getRestSeconds(),
                        exerciseDTO.getOrderPosition()
                );
            });
        }

        Workout updatedWorkout = workoutRepository.save(workout);
        return convertToDTO(updatedWorkout);
    }

    @Transactional
    public void deleteWorkout(Long id, Long userId) {
        // Verifica se o workout existe e pertence ao usuário
        if (!workoutRepository.existsByIdAndUserId(id, userId)) {
            throw new UnauthorizedAccessException("Você não tem permissão para excluir este treino");
        }

        workoutRepository.deleteById(id);
    }

    public Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
        return user.getId();
    }

    private WorkoutDTO convertToDTO(Workout workout) {
        List<WorkoutDTO.WorkoutExerciseDTO> exerciseDTOs = workout.getWorkoutExercises().stream()
                .sorted(Comparator.comparingInt(WorkoutExercise::getOrderPosition))
                .map(we -> new WorkoutDTO.WorkoutExerciseDTO(
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

        return new WorkoutDTO(
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

    private Workout convertToEntity(WorkoutDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + dto.getUserId()));

        Workout workout = new Workout();
        workout.setId(dto.getId());
        workout.setName(dto.getName());
        workout.setDescription(dto.getDescription());
        workout.setWorkoutType(dto.getWorkoutType());
        workout.setUser(user);
        workout.setWeekNumber(dto.getWeekNumber());
        workout.setDayNumber(dto.getDayNumber());

        if (dto.getWorkoutStatus() != null) {
            workout.setStatus(dto.getWorkoutStatus());
        }

        workout.setEstimatedDurationMinutes(dto.getEstimatedDurationMinutes());

        if (dto.getDifficultyLevel() != null) {
            workout.setDifficultyLevel(dto.getDifficultyLevel());
        }

        //Conversão de exercícios
        List<WorkoutExercise> workoutExercises = dto.getExercises().stream()
                .map(exDto -> {
                    Exercise exercise = exerciseRepository.findById(exDto.getExerciseId())
                            .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado com o id: " + exDto.getExerciseId()));

                    WorkoutExercise we = new WorkoutExercise();
                    we.setId(exDto.getId());
                    we.setWorkout(workout);
                    we.setExercise(exercise);
                    we.setSets(exDto.getSets());
                    we.setRepsPerSet(exDto.getRepsPerSet());
                    we.setWeight(exDto.getWeight());
                    we.setRestSeconds(exDto.getRestSeconds());
                    we.setOrderPosition(exDto.getOrderPosition());

                    return we;
                })
                .collect(Collectors.toList());

        workout.setWorkoutExercises(workoutExercises);

        // Mantém timestamps se já existirem (em caso de atualizações)
        workout.setCreatedAt(dto.getCreatedAt());
        workout.setUpdatedAt(dto.getUpdatedAt());

        return workout;
    }

}
