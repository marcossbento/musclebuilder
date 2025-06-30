package com.musclebuilder.service;

import com.musclebuilder.dto.ExerciseProgressDTO;
import com.musclebuilder.dto.ProgressSummaryDTO;
import com.musclebuilder.model.ExerciseLog;
import com.musclebuilder.model.WorkoutLogStatus;
import com.musclebuilder.repository.ExerciseLogRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
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

    @Autowired
    public ProgressService(WorkoutLogRepository workoutLogRepository, ExerciseLogRepository exerciseLogRepository) {
        this.workoutLogRepository = workoutLogRepository;
        this.exerciseLogRepository = exerciseLogRepository;
    }

    public ProgressSummaryDTO getGlobalSummary(Long userId) {
        long totalWorkouts = workoutLogRepository.countByUserIdAndStatus(userId, WorkoutLogStatus.COMPLETED);
        double totalVolume = exerciseLogRepository.findTotalVolumeByUserId(userId);
        String mostFrequentExercise = exerciseLogRepository.findMostFrequentExerciseByUserId(userId);

        return new ProgressSummaryDTO(totalWorkouts, totalVolume, mostFrequentExercise);
    }

    public List<ExerciseProgressDTO> getExerciseHistory(Long userId, Long exerciseId) {
        List<ExerciseLog> history = exerciseLogRepository.findExerciseHistory(userId, exerciseId);

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
