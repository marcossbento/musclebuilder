package com.musclebuilder.service;

import com.musclebuilder.dto.CompleteWorkoutResponseDTO;
import com.musclebuilder.dto.LogExerciseRequest;
import com.musclebuilder.dto.StartWorkoutRequest;
import com.musclebuilder.dto.WorkoutLogResponseDTO;
import com.musclebuilder.event.WorkoutCompletedEvent;
import com.musclebuilder.exception.ResourceNotFoundException;
import com.musclebuilder.exception.UnauthorizedAccessException;
import com.musclebuilder.mapper.WorkoutLogMapper;
import com.musclebuilder.model.*;
import com.musclebuilder.repository.ExerciseRepository;
import com.musclebuilder.repository.UserRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import com.musclebuilder.repository.WorkoutRepository;
import com.musclebuilder.service.security.SecurityContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    private final SecurityContextService securityContextService;
    private final ApplicationEventPublisher eventPublisher;
    private final WorkoutLogMapper workoutLogMapper;

    @Autowired
    public WorkoutLogService(
                                WorkoutLogRepository workoutLogRepository,
                                WorkoutRepository workoutRepository,
                                ExerciseRepository exerciseRepository,
                                GamificationService gamificationService,
                                SecurityContextService securityContextService,
                                ApplicationEventPublisher eventPublisher,
                                WorkoutLogMapper workoutLogMapper
    ){
        this.workoutLogRepository = workoutLogRepository;
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.securityContextService = securityContextService;
        this.eventPublisher = eventPublisher;
        this.workoutLogMapper = workoutLogMapper;
    }

    @Transactional
    public WorkoutLogResponseDTO startWorkout(StartWorkoutRequest req) {
        User currentUser = securityContextService.findCurrentUser();

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
        return workoutLogMapper.toDto(savedLog);
    }

    @Transactional
    public WorkoutLogResponseDTO logExercise(Long workoutLogId, LogExerciseRequest req) {
        User currentUser = securityContextService.findCurrentUser();

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
        return workoutLogMapper.toDto(updatedLog);
    }

    @Transactional
    public CompleteWorkoutResponseDTO completeWorkout(Long workoutLogId) {
        User currentUser = securityContextService.findCurrentUser();

        WorkoutLog workoutLog = workoutLogRepository.findById(workoutLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de treino não encontrado"));

        if (!workoutLog.getUser().equals(currentUser)) {
            throw new UnauthorizedAccessException("Você não tem permissão para alterar este registro");
        }

        workoutLog.completeWorkout();
        WorkoutLog completedLog = workoutLogRepository.save(workoutLog);

        WorkoutCompletedEvent event = new WorkoutCompletedEvent(completedLog);
        eventPublisher.publishEvent(event);

        List<Achievement> newAchievements = event.getNewlyAwardedAchievements();

        WorkoutLogResponseDTO logDTO = workoutLogMapper.toDto(completedLog);
        return new CompleteWorkoutResponseDTO(logDTO, newAchievements);
    }

    @Transactional(readOnly = true)
    public WorkoutLogResponseDTO getWorkoutLog(Long workoutLogId) {
        User currentUser = securityContextService.findCurrentUser();

        WorkoutLog workoutLog = workoutLogRepository.findByIdAndUserWithExerciseLogs(workoutLogId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de treino não encontrado com id: " + workoutLogId));

        return workoutLogMapper.toDto(workoutLog);
    }

    @Transactional(readOnly = true)
    public List<WorkoutLogResponseDTO> getAllUserWorkoutLogs() {
        User currentUser = securityContextService.findCurrentUser();
        List<WorkoutLog> logs = workoutLogRepository.findByUserWithExerciseLogs(currentUser);

        return logs.stream()
                .map(workoutLogMapper::toDto)
                .collect(Collectors.toList());
    }
}