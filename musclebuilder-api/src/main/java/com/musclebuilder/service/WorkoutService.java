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

        if (workoutDTO.exercises() != null) {
            workoutDTO.exercises().forEach(exerciseDTO -> {
                Exercise exercise = exerciseRepository.findById(exerciseDTO.exerciseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado"));

                workout.addExercise(
                        exercise,
                        exerciseDTO.sets(),
                        exerciseDTO.repsPerSet(),
                        exerciseDTO.weight(),
                        exerciseDTO.restSeconds(),
                        exerciseDTO.orderPosition()
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

        workout.setName(workoutDTO.name());
        workout.setDescription(workoutDTO.description());
        workout.setWorkoutType(workoutDTO.workoutType());
        workout.setWeekNumber(workoutDTO.weekNumber());
        workout.setDayNumber(workoutDTO.dayNumber());
        workout.setEstimatedDurationMinutes(workoutDTO.estimatedDurationMinutes());

        //Limpa e adiciona exercícios novamente
        workout.getWorkoutExercises().clear();

        if (workoutDTO.exercises() != null) {
            workoutDTO.exercises().forEach(exerciseDTO -> {
                Exercise exercise = exerciseRepository.findById(exerciseDTO.exerciseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado"));

                workout.addExercise(
                        exercise,
                        exerciseDTO.sets(),
                        exerciseDTO.repsPerSet(),
                        exerciseDTO.weight(),
                        exerciseDTO.restSeconds(),
                        exerciseDTO.orderPosition()
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
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + dto.userId()));

        Workout workout = new Workout();
        workout.setId(dto.id());
        workout.setName(dto.name());
        workout.setDescription(dto.description());
        workout.setWorkoutType(dto.workoutType());
        workout.setUser(user);
        workout.setWeekNumber(dto.weekNumber());
        workout.setDayNumber(dto.dayNumber());

        if (dto.workoutStatus() != null) {
            workout.setStatus(dto.workoutStatus());
        }

        workout.setEstimatedDurationMinutes(dto.estimatedDurationMinutes());

        if (dto.difficultyLevel() != null) {
            workout.setDifficultyLevel(dto.difficultyLevel());
        }

        //Conversão de exercícios
        List<WorkoutExercise> workoutExercises = dto.exercises().stream()
                .map(exDto -> {
                    Exercise exercise = exerciseRepository.findById(exDto.exerciseId())
                            .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado com o id: " + exDto.exerciseId()));

                    WorkoutExercise we = new WorkoutExercise();
                    we.setId(exDto.id());
                    we.setWorkout(workout);
                    we.setExercise(exercise);
                    we.setSets(exDto.sets());
                    we.setRepsPerSet(exDto.repsPerSet());
                    we.setWeight(exDto.weight());
                    we.setRestSeconds(exDto.restSeconds());
                    we.setOrderPosition(exDto.orderPosition());

                    return we;
                })
                .collect(Collectors.toList());

        workout.setWorkoutExercises(workoutExercises);

        // Mantém timestamps se já existirem (em caso de atualizações)
        workout.setCreatedAt(dto.createdAt());
        workout.setUpdatedAt(dto.updatedAt());

        return workout;
    }

}
