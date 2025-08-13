package com.musclebuilder.service;

import com.musclebuilder.dto.ExerciseProgressDTO;
import com.musclebuilder.dto.ProgressSummaryDTO;
import com.musclebuilder.model.ExerciseLog;
import com.musclebuilder.model.User;
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

    public ProgressSummaryDTO getSummaryForUser(User user) {
        long totalWorkouts = workoutLogRepository.countByUserAndStatus(user, WorkoutLogStatus.COMPLETED);
        Double totalVolume = exerciseLogRepository.findTotalVolumeByUser(user);
        String mostFrequentExercise = exerciseLogRepository.findMostFrequentExerciseByUser(user);

        return new ProgressSummaryDTO(
                totalWorkouts,
                totalVolume != null ? totalVolume : 0.0,
                mostFrequentExercise);
    }

    public List<ExerciseProgressDTO> getExerciseHistoryForUser(User user, Long exerciseId) {
        List<ExerciseLog> history = exerciseLogRepository.findExerciseHistoryForUser(user, exerciseId);

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
