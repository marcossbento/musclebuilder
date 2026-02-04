package com.musclebuilder.repository;

import com.musclebuilder.AbstractIntegrationTest;
import com.musclebuilder.model.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class WorkoutLogRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private WorkoutLogRepository workoutLogRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    private User testUser;
    private Exercise testExercise;

    @BeforeEach
    void setUp() {
        // Limpeza via Repository para garantir triggers do Spring Data se houver
        workoutLogRepository.deleteAll();
        exerciseRepository.deleteAll();
        userRepository.deleteAll();

        // User de teste
        testUser = new User();
        testUser.setName("João Teste");
        testUser.setEmail("joao@teste.com");
        testUser.setPassword("senha123");
        testUser.setHeight("180");
        testUser.setWeight("80");
        testUser.setGoal("Ganhar massa");
        testUser = userRepository.save(testUser);

        testExercise = new Exercise();
        testExercise.setName("Supino Reto Teste");
        testExercise.setMuscleGroup(MuscleGroup.CHEST);
        testExercise.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
        testExercise = exerciseRepository.save(testExercise);
    }

    @Test
    void contextLoads() {
        assertThat(workoutLogRepository).isNotNull();
    }

    @Test
    void deveSalvarWorkoutLog() {
        // ARRANGE
        WorkoutLog workoutLog = new WorkoutLog();
        workoutLog.setUser(testUser);
        workoutLog.setWorkoutName("Treino de Peito");
        workoutLog.setStatus(WorkoutLogStatus.IN_PROGRESS);
        workoutLog.setStartedAt(LocalDateTime.now());

        // ACT
        WorkoutLog savedLog = workoutLogRepository.save(workoutLog);

        // ASSERT
        assertThat(savedLog).isNotNull();
        assertThat(savedLog.getId()).isNotNull();
        assertThat(savedLog.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedLog.getCreatedAt()).isNotNull();
    }

    @Test
    void deveEncontrarWorkoutLogsPorUsuarioComPaginacao() {
        // ARRANGE
        WorkoutLog log1 = new WorkoutLog();
        log1.setUser(testUser);
        log1.setWorkoutName("Treino A - Mais Antigo");
        log1.setStatus(WorkoutLogStatus.COMPLETED);
        log1.setStartedAt(LocalDateTime.now().minusDays(2));
        workoutLogRepository.save(log1);

        WorkoutLog log2 = new WorkoutLog();
        log2.setUser(testUser);
        log2.setWorkoutName("Treino B - Mais Recente");
        log2.setStatus(WorkoutLogStatus.COMPLETED);
        log2.setStartedAt(LocalDateTime.now().minusDays(1));
        workoutLogRepository.save(log2);

        // ACT
        Page<WorkoutLog> resultPage = workoutLogRepository
                .findByUserOrderByStartedAtDesc(testUser, PageRequest.of(0, 5));

        // ASSERT
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(2);
        assertThat(resultPage.getContent()).hasSize(2);
        // O mais recente deve vir primeiro
        assertThat(resultPage.getContent().get(0).getWorkoutName()).isEqualTo("Treino B - Mais Recente");
    }

    @Test
    void deveManterRelacionamentoComExerciseLogs() {
        // ARRANGE
        WorkoutLog workoutLog = new WorkoutLog();
        workoutLog.setUser(testUser);
        workoutLog.setWorkoutName("Treino de Costas");
        workoutLog.setStatus(WorkoutLogStatus.IN_PROGRESS);
        workoutLog.setStartedAt(LocalDateTime.now());

        ExerciseLog exerciseLog = new ExerciseLog();
        exerciseLog.setExercise(testExercise);
        exerciseLog.setExerciseName("Remada Curvada");
        exerciseLog.setSetsCompleted(3);
        exerciseLog.addSet(10, 50.0);

        workoutLog.addExerciseLog(exerciseLog);

        // Salva o pai (Cascade deve salvar o filho)
        workoutLogRepository.save(workoutLog);

        // Limpa o cache do Hibernate para forçar um SELECT novo do banco
        entityManager.flush();
        entityManager.clear();

        // ACT
        // Recarrega o usuário para garantir que está anexado à sessão
        User managedUser = userRepository.findById(testUser.getId()).orElseThrow();
        List<WorkoutLog> retrievedLogs = workoutLogRepository.findByUserWithExerciseLogs(managedUser);

        // ASSERT
        assertThat(retrievedLogs).isNotNull().hasSize(1);
        WorkoutLog firstLog = retrievedLogs.get(0);
        assertThat(firstLog.getExerciseLogs()).isNotEmpty();
        assertThat(firstLog.getExerciseLogs()).hasSize(1);
        assertThat(firstLog.getExerciseLogs().get(0).getExerciseName()).isEqualTo("Remada Curvada");
    }
}