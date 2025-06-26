package com.musclebuilder.repository;

import com.musclebuilder.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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

        //Salvar no banco de teste e garantir a persistência
        testUser = entityManager.persistAndFlush(testUser);

        // Verificar se usuário foi criado
        assertThat(testUser).isNotNull();
        assertThat(testUser.getId()).isNotNull();

        // Criar exercício - INICIALIZAR EXPLICITAMENTE
        testExercise = new Exercise();

        // Verificar se exercício foi instanciado
        assertThat(testExercise).isNotNull();

        //Workout de teste
        testExercise.setName("Treino teste");
        testExercise.setMuscleGroup("Peito");
        testExercise.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);

        testExercise = entityManager.persistAndFlush(testExercise);

        // Verificar se exercício foi persistido
        assertThat(testExercise).isNotNull();
        assertThat(testExercise.getId()).isNotNull();

    }

    @Test
    void contextLoads() {
        assertThat(workoutLogRepository).isNotNull();
    }

    @Test
    void deveSalvarWorkoutLog() {

        WorkoutLog workoutLog = new WorkoutLog();
        workoutLog.setUser(testUser);
        workoutLog.setWorkoutName("Treino 1");
        workoutLog.setStatus(WorkoutLogStatus.IN_PROGRESS);

        WorkoutLog savedLog = workoutLogRepository.save(workoutLog);


        assertThat(savedLog.getId()).isNotNull();
        assertThat(savedLog.getWorkoutName()).isEqualTo("Treino 1");
        assertThat(savedLog.getUser()).isNotNull();
        assertThat(savedLog.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedLog.getCreatedAt()).isNotNull();

    }
    /*
    @Test
    void deveEncontrarWorkoutLogsPorUsuario() {
        //Criar múltiplos logs para o mesmo usuário
        WorkoutLog log1 = new WorkoutLog();
        log1.setUser(testUser);
        log1.setWorkoutName("Treino A");
        log1.setStartedAt(LocalDateTime.now().minusDays(2));
        log1.setStatus(WorkoutLogStatus.COMPLETED);

        WorkoutLog log2 = new WorkoutLog();
        log2.setUser(testUser);
        log2.setWorkoutName("Treino B");
        log2.setStartedAt(LocalDateTime.now().minusDays(1));
        log2.setStatus(WorkoutLogStatus.COMPLETED);

        //Salvar ambos
        workoutLogRepository.save(log1);
        workoutLogRepository.save(log2);

        //Buscar por usuário e status
        List<WorkoutLog> logsCompletos = workoutLogRepository
                .findByUserIdAndStatusOrderByStartedAtDesc(testUser.getId(), WorkoutLogStatus.COMPLETED);

        //Verificação
        assertThat(logsCompletos).hasSize(2);
        assertThat(logsCompletos.get(0).getWorkoutName()).isEqualTo("Treino B"); // Mais recente primeiro
        assertThat(logsCompletos.get(1).getWorkoutName()).isEqualTo("Treino A");
    } */

    @Test
    void deveManterRelacionamentoComExerciseLogs() {
        // ARRANGE
        // Criar workout log
        WorkoutLog workoutLog = new WorkoutLog();
        workoutLog.setUser(testUser);
        workoutLog.setWorkoutName("Treino Peito");
        workoutLog.setStatus(WorkoutLogStatus.IN_PROGRESS);


        workoutLog = workoutLogRepository.saveAndFlush(workoutLog);

        // Criar exercise log
        ExerciseLog exerciseLog = new ExerciseLog();
        exerciseLog.setExercise(testExercise);
        exerciseLog.setExerciseName("Supino");
        exerciseLog.setSetsCompleted(3);
        exerciseLog.setRepsPerSet("12,10,8");
        exerciseLog.setWeightUsed(80.0);
        exerciseLog.setOrderPosition(1);

        workoutLog.addExerciseLog(exerciseLog);

        workoutLogRepository.saveAndFlush(workoutLog);

        entityManager.clear();

        // ACT - Recuperar do banco
        Optional<WorkoutLog> retrieved = workoutLogRepository.findByIdWithExerciseLogs(workoutLog.getId());

        // ASSERT
        assertThat(retrieved).isPresent();
        WorkoutLog workoutLogWithLogs = retrieved.get();
        assertThat(workoutLogWithLogs.getExerciseLogs()).isNotEmpty();
        assertThat(workoutLogWithLogs.getExerciseLogs()).hasSize(1);

        ExerciseLog retrievedExerciseLog = workoutLogWithLogs.getExerciseLogs().get(0);
        assertThat(retrievedExerciseLog.getExerciseName()).isEqualTo("Supino");
        assertThat(retrievedExerciseLog.getWeightUsed()).isEqualTo(80.0);
    }


}
