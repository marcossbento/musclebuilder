package com.musclebuilder.service;

import com.musclebuilder.dto.ProgressSummaryDTO;
import com.musclebuilder.model.User;
import com.musclebuilder.model.WorkoutLogStatus;
import com.musclebuilder.repository.ExerciseLogRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import org.junit.jupiter.api.BeforeEach;
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

    @Mock
    private UserService userService;

    @InjectMocks
    private ProgressService progressService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Usuário de Teste");
        testUser.setEmail("teste@email.com");
    }

    @Test
    void quandoGetSummaryForCurrentUser_deveRetornarResumoCorreto() {
        //ARRANGE
        when(userService.findCurrentUser()).thenReturn(testUser);

        when(workoutLogRepository.countByUserAndStatus(testUser, WorkoutLogStatus.COMPLETED))
                .thenReturn(15L);

        when(exerciseLogRepository.findTotalVolumeByUser(testUser))
                .thenReturn(12500.5);

        when(exerciseLogRepository.findMostFrequentExerciseByUser(testUser))
                .thenReturn("Supino Reto");

        //ACT
        ProgressSummaryDTO summary = progressService.getSummaryForCurrentUser();

        //ASSERT
        assertThat(summary).isNotNull();
        assertThat(summary.totalWorkouts()).isEqualTo(15L);
        assertThat(summary.totalVolume()).isEqualTo(12500.5);
        assertThat(summary.mostFrequentExercise()).isEqualTo("Supino Reto");
    }

    @Test
    void quandoNaoHaDados_getSummary_ForUser_deveRetornarValoresPadrao() {
        //ARRANGE
        when(userService.findCurrentUser()).thenReturn(testUser);

        when(workoutLogRepository.countByUserAndStatus(testUser, WorkoutLogStatus.COMPLETED))
                .thenReturn(0L);
        when(exerciseLogRepository.findTotalVolumeByUser(testUser))
                .thenReturn(0.0);
        when(exerciseLogRepository.findMostFrequentExerciseByUser(testUser))
                .thenReturn(null);;

        //ACT
        ProgressSummaryDTO summary = progressService.getSummaryForCurrentUser();

        //ASSERT
        assertThat(summary).isNotNull();
        assertThat(summary.totalWorkouts()).isEqualTo(0L);
        assertThat(summary.totalVolume()).isEqualTo(0.0);
        assertThat(summary.mostFrequentExercise()).isNull();

    }

}
