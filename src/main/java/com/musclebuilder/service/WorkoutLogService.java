package com.musclebuilder.service;

import com.musclebuilder.dto.LogExerciseRequest;
import com.musclebuilder.dto.StartWorkoutRequest;
import com.musclebuilder.dto.WorkoutLogResponseDTO;
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

    private final GamificationService gamificationService;

    @Autowired
    public WorkoutLogService(WorkoutLogRepository workoutLogRepository, UserRepository userRepository, WorkoutRepository workoutRepository, ExerciseRepository exerciseRepository, GamificationService gamificationService) {
        this.workoutLogRepository = workoutLogRepository;
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.gamificationService = gamificationService;
    }

    @Transactional
    public WorkoutLogResponseDTO startWorkout(Long userId, StartWorkoutRequest req) {
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
        return mapToResponseDTO(savedLog);
    }

    @Transactional
    public WorkoutLogResponseDTO logExercise(Long workoutLogId, Long userId, LogExerciseRequest req) {
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
        return mapToResponseDTO(updatedLog);
    }

    @Transactional
    public WorkoutLogResponseDTO completeWorkout(Long workoutLogId, Long userId) {
        WorkoutLog workoutLog = workoutLogRepository.findById(workoutLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de treino não encontrado"));

        if (!workoutLog.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Você não tem permissão para alterar este registro");
        }

        workoutLog.completeWorkout();

        WorkoutLog completedLog = workoutLogRepository.save(workoutLog);

        //Após a conclusão do treino, o serviço de gamificação é chamado.
        gamificationService.checkAndAwardAchievements(userId);

        return mapToResponseDTO(completedLog);
    }

    @Transactional(readOnly = true)
    public WorkoutLogResponseDTO getWorkoutLog(Long workoutLogId, Long userId) {
        WorkoutLog workoutLog = workoutLogRepository.findByIdWithExerciseLogs(workoutLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de treino não encontrado com id: " + workoutLogId));

        if (!workoutLog.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Você não tem permissão para visualizar esse registro de treino");
        }

        return mapToResponseDTO(workoutLog);
    }

    @Transactional(readOnly = true)
    public List<WorkoutLogResponseDTO> getAllUserWorkoutLogs(Long userId) {
        List<WorkoutLog> logs = workoutLogRepository.findByUserIdWithExerciseLogs(userId);

        return logs.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private WorkoutLogResponseDTO mapToResponseDTO(WorkoutLog workoutLog) {
        Workout workoutTemplate = workoutLog.getWorkout();

        List<WorkoutLogResponseDTO.ExerciseLogResponseDTO> exerciseLogDTOs = workoutLog.getExerciseLogs().stream()
                .map(log -> {
                    WorkoutExercise templateExercise = workoutTemplate.getWorkoutExercises().stream()
                            .filter(we -> we.getExercise().getId().equals(log.getExercise().getId()))
                            .findFirst()
                            .orElse(null);

                    return new WorkoutLogResponseDTO.ExerciseLogResponseDTO(
                            log.getId(),
                            log.getExerciseName(),
                            templateExercise != null ? templateExercise.getSets() : null,
                            templateExercise != null ? templateExercise.getRepsPerSet() : null,
                            log.getSetsCompleted(),
                            log.getRepsPerSet(),
                            log.getWeightUsed()
                    );
                })
                .collect(Collectors.toList());

        return new WorkoutLogResponseDTO(
                workoutLog.getId(),
                workoutLog.getWorkoutName(),
                workoutLog.getStatus(),
                workoutLog.getStartedAt(),
                workoutLog.getCompletedAt(),
                workoutLog.getDurationMinutes(),
                workoutLog.getTotalVolume(),
                exerciseLogDTOs
        );
    }
}