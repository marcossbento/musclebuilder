package com.musclebuilder.repository;

import com.musclebuilder.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class WorkoutLogRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WorkoutLogRepository workoutLogRepository;

    private User testUser;
    private Exercise testExercise;

    @BeforeEach
    void setUp() {
        //User de teste
        testUser = new User();
        testUser.setName("João Teste");
        testUser.setEmail("joao@teste.com");
        testUser.setPassword("senha123");
        testUser.setHeight("180");
        testUser.setWeight("80");
        testUser.setGoal("Ganhar massa");
        testUser = entityManager.persistAndFlush(testUser);

        testExercise = new Exercise();
        testExercise.setName("Supino Reto Teste");
        testExercise.setMuscleGroup(MuscleGroup.CHEST);
        testExercise.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
        testExercise = entityManager.persistAndFlush(testExercise);
    }

    @Test
    void contextLoads() {
        assertThat(workoutLogRepository).isNotNull();
    }

    @Test
    @DisplayName("Deve salvar um WorkoutLog com sucesso")
    void deveSalvarWorkoutLog() {
        // ARRANGE
        WorkoutLog workoutLog = new WorkoutLog();
        workoutLog.setUser(testUser);
        workoutLog.setWorkoutName("Treino de Peito");
        workoutLog.setStatus(WorkoutLogStatus.IN_PROGRESS);

        // ACT
        WorkoutLog savedLog = workoutLogRepository.save(workoutLog);

        //ASSERT
        assertThat(savedLog).isNotNull();
        assertThat(savedLog.getId()).isNotNull();
        assertThat(savedLog.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedLog.getCreatedAt()).isNotNull();

    }
    @Test
    @DisplayName("Deve encontrar os workoutLogs de um usuário de forma paginada e ordenada")
    void deveEncontrarWorkoutLogsPorUsuarioComPaginacao() {
        //ARRANGE - Criar múltiplos logs para o mesmo usuário
        WorkoutLog log1 = new WorkoutLog();
        log1.setUser(testUser);
        log1.setWorkoutName("Treino A - Mais Antigo");
        log1.setStartedAt(LocalDateTime.now().minusDays(2));
        entityManager.persist(log1);

        WorkoutLog log2 = new WorkoutLog();
        log2.setUser(testUser);
        log2.setWorkoutName("Treino B - Mais Recente");
        log2.setStartedAt(LocalDateTime.now().minusDays(1));
        entityManager.persist(log2);

        entityManager.flush();

        // ACT
        Page<WorkoutLog> resultPage = workoutLogRepository
                .findByUserOrderByStartedAtDesc(testUser, PageRequest.of(0, 5));

        // ASSERT
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(2);
        assertThat(resultPage.getContent()).hasSize(2);
        assertThat(resultPage.getContent().get(0).getWorkoutName()).isEqualTo("Treino B - Mais Recente");
    }

    @Test
    void deveManterRelacionamentoComExerciseLogs() {
        // ARRANGE
        // Criar workout log
        WorkoutLog workoutLog = new WorkoutLog();
        workoutLog.setUser(testUser);
        workoutLog.setWorkoutName("Treino de Costas");
        workoutLog.setStatus(WorkoutLogStatus.IN_PROGRESS);

        // Criar exercise log
        ExerciseLog exerciseLog = new ExerciseLog();
        exerciseLog.setExercise(testExercise);
        exerciseLog.setExerciseName("Remada Curvada");
        exerciseLog.setSetsCompleted(3);
        exerciseLog.setRepsPerSet("10,10,10");

        workoutLog.addExerciseLog(exerciseLog);
        entityManager.persistAndFlush(workoutLog);
        entityManager.clear();

        // ACT
        List<WorkoutLog> retrievedLogs = workoutLogRepository.findByUserWithExerciseLogs(testUser);

        // ASSERT
        assertThat(retrievedLogs).isNotNull().hasSize(1);
        WorkoutLog firstLog = retrievedLogs.get(0);
        assertThat(firstLog.getExerciseLogs()).isNotEmpty();
        assertThat(firstLog.getExerciseLogs()).hasSize(1);

        ExerciseLog retrievedExerciseLog = firstLog.getExerciseLogs().get(0);
        assertThat(retrievedExerciseLog.getExerciseName()).isEqualTo("Remada Curvada");
    }


}
