package com.musclebuilder.service;

import com.musclebuilder.dto.CompleteWorkoutResponseDTO;
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
    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final GamificationService gamificationService;
    private final UserService userService;

    @Autowired
    public WorkoutLogService(WorkoutLogRepository workoutLogRepository, WorkoutRepository workoutRepository, ExerciseRepository exerciseRepository, GamificationService gamificationService, UserService userService) {
        this.workoutLogRepository = workoutLogRepository;
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.gamificationService = gamificationService;
        this.userService = userService;
    }

    @Transactional
    public WorkoutLogResponseDTO startWorkout(StartWorkoutRequest req) {
        User currentUser = userService.findCurrentUser();

        WorkoutLog newLog = new WorkoutLog();
        newLog.setUser(currentUser);
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
    public WorkoutLogResponseDTO logExercise(Long workoutLogId, LogExerciseRequest req) {
        User currentUser = userService.findCurrentUser();

        WorkoutLog workoutLog = workoutLogRepository.findById(workoutLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de treino não encontrado"));

        if (!workoutLog.getUser().equals(currentUser)) {
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
    public CompleteWorkoutResponseDTO completeWorkout(Long workoutLogId) {
        User currentUser = userService.findCurrentUser();

        WorkoutLog workoutLog = workoutLogRepository.findById(workoutLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de treino não encontrado"));

        if (!workoutLog.getUser().equals(currentUser)) {
            throw new UnauthorizedAccessException("Você não tem permissão para alterar este registro");
        }

        workoutLog.completeWorkout();
        WorkoutLog completedLog = workoutLogRepository.save(workoutLog);

        //Após a conclusão do treino, o serviço de gamificação é chamado(Concede XP e conquistas).
        gamificationService.awardXpForWorkout(currentUser, completedLog);
        List<Achievement> newAchievements = gamificationService.checkAndAwardAchievements();

        WorkoutLogResponseDTO logDTO = mapToResponseDTO(completedLog);
        return new CompleteWorkoutResponseDTO(logDTO, newAchievements);
    }

    @Transactional(readOnly = true)
    public WorkoutLogResponseDTO getWorkoutLog(Long workoutLogId) {
        User currentUser = userService.findCurrentUser();

        WorkoutLog workoutLog = workoutLogRepository.findByIdAndUserWithExerciseLogs(workoutLogId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de treino não encontrado com id: " + workoutLogId));

        return mapToResponseDTO(workoutLog);
    }

    @Transactional(readOnly = true)
    public List<WorkoutLogResponseDTO> getAllUserWorkoutLogs() {
        User currentUser = userService.findCurrentUser();
        List<WorkoutLog> logs = workoutLogRepository.findByUserWithExerciseLogs(currentUser);

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