package com.musclebuilder.service;

import com.musclebuilder.dto.LogExerciseRequest;
import com.musclebuilder.dto.StartWorkoutRequest;
import com.musclebuilder.dto.WorkoutLogResponse;
import com.musclebuilder.exception.ResourceNotFoundException;
import com.musclebuilder.exception.UnauthorizedAccessException;
import com.musclebuilder.model.*;
import com.musclebuilder.repository.ExerciseRepository;
import com.musclebuilder.repository.UserRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import com.musclebuilder.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutLogService {

    private final WorkoutLogRepository workoutLogRepository;
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;

    @Autowired
    public WorkoutLogService (WorkoutLogRepository workoutLogRepository, UserRepository userRepository, WorkoutRepository workoutRepository, ExerciseRepository exerciseRepository) {
        this.workoutLogRepository = workoutLogRepository;
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional
    public WorkoutLogResponse startWorkout(Long userId, StartWorkoutRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        WorkoutLog newLog = new WorkoutLog();
        newLog.setUser(user);
        newLog.setWorkoutName(req.workoutName());
        newLog.setStatus(WorkoutLogStatus.IN_PROGRESS);

        if (req.workoutId() != null) {
            Workout template = workoutRepository.findById(req.workoutId())
                    .orElseThrow(() -> new ResourceNotFoundException("Template de treino não encontrado"));
            newLog.setWorkout(template);
        }

        WorkoutLog savedLog = workoutLogRepository.save(newLog);
        return convertToResponse(savedLog);
    }

    @Transactional
    public WorkoutLogResponse logExercise(Long workoutLogId, Long userId, LogExerciseRequest req) {
        WorkoutLog workoutLog = workoutLogRepository.findById(workoutLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de treino não encontrado"));

        if (!workoutLog.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Você não tem permissão para alterar esse registro");
        }

        Exercise exercise = exerciseRepository.findById(req.exerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado"));

        ExerciseLog newExerciseLog = new ExerciseLog();
        newExerciseLog.setExercise(exercise);
        newExerciseLog.setExerciseName(exercise.getName());
        newExerciseLog.setSetsCompleted(req.setsCompleted());
        newExerciseLog.setRepsPerSet(req.repsPerSet());
        newExerciseLog.setWeightUsed(req.weightUsed());
        newExerciseLog.setNotes(req.notes());

        workoutLog.addExerciseLog(newExerciseLog);

        WorkoutLog updatedLog = workoutLogRepository.save(workoutLog);
        return convertToResponse(updatedLog);
    }

    @Transactional
    public WorkoutLogResponse completeWorkout(Long workoutLogId, Long userId) {
        WorkoutLog workoutLog = workoutLogRepository.findById(workoutLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de treino não encontrado"));

        if (!workoutLog.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Você não tem permissão para alterar este registro");
        }

        workoutLog.completeWorkout();

        WorkoutLog completedLog = workoutLogRepository.save(workoutLog);
        return convertToResponse(completedLog);
    }

    @Transactional(readOnly = true)
    public WorkoutLogResponse getWorkoutLog(Long workoutLogId, Long userId) {
        WorkoutLog workoutLog = workoutLogRepository.findByIdWithExerciseLogs(workoutLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de treino não encontrado com id: " + workoutLogId));

        if (!workoutLog.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Você não tem permissão para visualizar esse registro de treino");
        }

        return convertToResponse(workoutLog);
    }

    @Transactional(readOnly = true)
    public List<WorkoutLogResponse> getAllUserWorkoutLogs(Long userId) {
        List<WorkoutLog> logs = workoutLogRepository.findByUserIdWithExerciseLogs(userId);

        return logs.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
    }

    private WorkoutLogResponse convertToResponse(WorkoutLog log) {
        //Converte a lista de entidades ExerciseLog para uma lista de DTOs ExerciseLogDetails
        List<WorkoutLogResponse.ExerciseLogDetails> exerciseDetails = log.getExerciseLogs()
                .stream()
                .map(exerciseLog -> new WorkoutLogResponse.ExerciseLogDetails(
                        exerciseLog.getId(),
                        exerciseLog.getExercise().getId(),
                        exerciseLog.getExercise().getName(),
                        exerciseLog.getSetsCompleted(),
                        exerciseLog.getRepsPerSet(),
                        exerciseLog.getWeightUsed(),
                        exerciseLog.getNotes()
                ))
                .collect(Collectors.toList());

        //Calcula a duração do treino em minutos
        //Checa primeiro se o treino já foi completado com nullCheck do completedAt
        Long duration = null;
        if (log.getCompletedAt() != null) {
            duration = Duration.between(log.getStartedAt(), log.getCompletedAt()).toMinutes();
        }

        return new WorkoutLogResponse(
                log.getId(),
                log.getWorkoutName(),
                log.getStatus(),
                log.getStartedAt(),
                log.getCompletedAt(),
                duration,
                log.getTotalVolume(),
                exerciseDetails
        );
    }

}
