package com.musclebuilder.service;

import com.musclebuilder.dto.ExerciseProgressDTO;
import com.musclebuilder.dto.ProgressSummaryDTO;
import com.musclebuilder.model.ExerciseLog;
import com.musclebuilder.model.User;
import com.musclebuilder.model.WorkoutLogStatus;
import com.musclebuilder.repository.ExerciseLogRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import com.musclebuilder.service.security.SecurityContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProgressService {

    private final WorkoutLogRepository workoutLogRepository;
    private final ExerciseLogRepository exerciseLogRepository;
    private final SecurityContextService securityContextService;

    @Autowired
    public ProgressService(WorkoutLogRepository workoutLogRepository, ExerciseLogRepository exerciseLogRepository, SecurityContextService securityContextService) {
        this.workoutLogRepository = workoutLogRepository;
        this.exerciseLogRepository = exerciseLogRepository;
        this.securityContextService = securityContextService;
    }

    public ProgressSummaryDTO getSummaryForCurrentUser() {
        User currentUser = securityContextService.findCurrentUser();
        long totalWorkouts = workoutLogRepository.countByUserAndStatus(currentUser, WorkoutLogStatus.COMPLETED);
        Double totalVolume = exerciseLogRepository.findTotalVolumeByUser(currentUser);
        String mostFrequentExercise = exerciseLogRepository.findMostFrequentExerciseByUser(currentUser);

        return new ProgressSummaryDTO(
                totalWorkouts,
                totalVolume != null ? totalVolume : 0.0,
                mostFrequentExercise);
    }

    public List<ExerciseProgressDTO> getExerciseHistoryForCurrentUser(Long exerciseId) {
        User currentUser = securityContextService.findCurrentUser();
        List<ExerciseLog> history = exerciseLogRepository.findExerciseHistoryForUser(currentUser, exerciseId);

        //converte a lista de entidades de log para a lista de DTOs de progresso
        return history.stream()
                .map(log -> new ExerciseProgressDTO(
                        log.getCreatedAt().toLocalDate(),
                        log.getMaxWeight() != null ? log.getMaxWeight() : 0.0,
                        log.getVolume() != null ? log.getVolume() : 0.0
                ))
                .collect(Collectors.toList());
    }

}
