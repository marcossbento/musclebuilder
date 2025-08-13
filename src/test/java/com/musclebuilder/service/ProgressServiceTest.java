package com.musclebuilder.service;

import com.musclebuilder.dto.ProgressSummaryDTO;
import com.musclebuilder.model.WorkoutLogStatus;
import com.musclebuilder.repository.ExerciseLogRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProgressServiceTest {

    @Mock
    private WorkoutLogRepository workoutLogRepository;

    @Mock
    private ExerciseLogRepository exerciseLogRepository;

    @InjectMocks
    private ProgressService progressService;

    private final Long testUserId = 1L;

    @Test
    void quandoGetSummary_ForUser_deveRetornarResumoCorreto() {
        //ARRANGE
        when(workoutLogRepository.countByUserIdAndStatus(testUserId, WorkoutLogStatus.COMPLETED))
                .thenReturn(15L);

        when(exerciseLogRepository.findTotalVolumeByUserId(testUserId))
                .thenReturn(12500.5);

        when(exerciseLogRepository.findMostFrequentExerciseByUserId(testUserId))
                .thenReturn("Supino Reto");

        //ACT
        ProgressSummaryDTO summary = progressService.getSummaryForUser(testUserId);

        //ASSERT
        assertThat(summary).isNotNull();
        assertThat(summary.totalWorkouts()).isEqualTo(15L);
        assertThat(summary.totalVolume()).isEqualTo(12500.5);
        assertThat(summary.mostFrequentExercise()).isEqualTo("Supino Reto");
    }

    @Test
    void quandoNaoHaDados_getSummary_ForUser_deveRetornarValoresPadrao() {

        //ARRANGE
        when(workoutLogRepository.countByUserIdAndStatus(testUserId, WorkoutLogStatus.COMPLETED))
                .thenReturn(0L);
        when(exerciseLogRepository.findTotalVolumeByUserId(testUserId))
                .thenReturn(0.0);
        when(exerciseLogRepository.findMostFrequentExerciseByUserId(testUserId))
                .thenReturn(null);;

        //ACT
        ProgressSummaryDTO summary = progressService.getSummaryForUser(testUserId);

        //ASSERT
        assertThat(summary).isNotNull();
        assertThat(summary.totalWorkouts()).isEqualTo(0L);
        assertThat(summary.totalVolume()).isEqualTo(0.0);
        assertThat(summary.mostFrequentExercise()).isNull();

    }

}
