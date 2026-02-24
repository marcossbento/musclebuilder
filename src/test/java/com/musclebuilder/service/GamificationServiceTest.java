package com.musclebuilder.service;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;
import com.musclebuilder.repository.*;
import com.musclebuilder.service.achievements.FirstWorkoutAchievementChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationServiceTest {

    @Mock
    private FirstWorkoutAchievementChecker firstWorkoutAchievementChecker;

    private GamificationService gamificationService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Usuário Teste");
        testUser.setEmail("teste@email.com");

        firstWorkoutAchievementChecker = mock(FirstWorkoutAchievementChecker.class);

        gamificationService = new GamificationService(
                mock(WorkoutLogRepository.class),
                mock(MissionCompletionRepository.class),
                List.of(firstWorkoutAchievementChecker),
                List.of(),
                mock(com.musclebuilder.config.GamificationProperties.class));
    }

    @Test
    void quandoVerificarConquistas_deveChamarTodosOsCheckers() {
        when(firstWorkoutAchievementChecker.check(testUser)).thenReturn(Optional.empty());

        List<Achievement> newAchievements = gamificationService.checkAndAwardAchievements(testUser);

        verify(firstWorkoutAchievementChecker, times(1)).check(testUser);

        assertTrue(newAchievements.isEmpty());
    }

    @Test
    void quandoUmCheckerEncontraUmaConquista_deveRetornarNaLista() {

        // ARRANGE
        Achievement fakeAchievement = new Achievement();
        fakeAchievement.setName("Primeiro Treino");

        // Ensina o checker a devolver a conquista falsa.
        when(firstWorkoutAchievementChecker.check(testUser)).thenReturn(Optional.of(fakeAchievement));

        // ACT
        List<Achievement> newAchievements = gamificationService.checkAndAwardAchievements(testUser);

        // ASSERT
        verify(firstWorkoutAchievementChecker, times(1)).check(testUser);

        assertFalse(newAchievements.isEmpty());
        assertTrue(newAchievements.contains(fakeAchievement));

    }
}
