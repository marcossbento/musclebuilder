package com.musclebuilder.service;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;
import com.musclebuilder.model.WorkoutLogStatus;
import com.musclebuilder.repository.AchievementRepository;
import com.musclebuilder.repository.ExerciseLogRepository;
import com.musclebuilder.repository.UserRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GamificationServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkoutLogRepository workoutLogRepository;

    @Mock
    private ExerciseLogRepository exerciseLogRepository;

    @InjectMocks
    private GamificationService gamificationService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Usuário Teste");
        testUser.setEmail("teste@email.com");
    }

    @Test
    void quandoUsuarioCompletaPrimeiroTreino_deveConcederConquista() {
        //ARRANGE
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        when(achievementRepository.existsByUserAndName(1L, "Primeiro Treino")).thenReturn(false);

        when(workoutLogRepository.countByUserIdAndStatus(1L, WorkoutLogStatus.COMPLETED)).thenReturn(1L);

        //ACT
        gamificationService.checkAndAwardAchievements(1L);

        //ASSERT
        verify(achievementRepository, times(1)).save(any(Achievement.class));
    }

    @Test
    void quandoUsuarioJaTemConquista_naoDeveConcederNovamente() {

        //ARRANGE
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        //Neste teste simula que o user JÁ possui a conquista
        when(achievementRepository.existsByUserAndName(1L, "Primeiro Treino")).thenReturn(true);

        //ACT
        gamificationService.checkAndAwardAchievements(1L);

        //ASSERT
        verify(achievementRepository, never()).save(any(Achievement.class));

    }

    @Test
    void quandoUsuarioAindaNaoCompletouTreino_naoDeveConcederConquista() {

        //ARRANGE
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(achievementRepository.existsByUserAndName(1L, "Primeiro Treino")).thenReturn(false);

        when(workoutLogRepository.countByUserIdAndStatus(1L, WorkoutLogStatus.COMPLETED)).thenReturn(0L);

        //ACT
        gamificationService.checkAndAwardAchievements(1L);

        //ASSERT
        verify(achievementRepository, never()).save(any(Achievement.class));

    }
}
