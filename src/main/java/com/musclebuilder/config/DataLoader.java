package com.musclebuilder.config;

import com.musclebuilder.model.*;
import com.musclebuilder.repository.ExerciseRepository;
import com.musclebuilder.repository.UserRepository;
import com.musclebuilder.repository.WorkoutLogRepository;
import com.musclebuilder.repository.WorkoutRepository;
import com.musclebuilder.service.GamificationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final GamificationService gamificationService;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository,
            ExerciseRepository exerciseRepository,
            WorkoutRepository workoutRepository,
            WorkoutLogRepository workoutLogRepository,
            GamificationService gamificationService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutRepository = workoutRepository;
        this.workoutLogRepository = workoutLogRepository;
        this.gamificationService = gamificationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println(">>> Povoando banco de dados com  dados iniciais...");
            User savedUser = loadAndSaveUser();

            Map<String, Exercise> exerciseByName = loadAndSaveExercises();

            loadWorkouts(savedUser, exerciseByName);

            loadHistory(savedUser, exerciseByName);

            System.out.println(">>> Povoamento concluído");
        } else {
            System.out.println(">>> O banco de dados já contem dados. Povoamento cancelado");
        }
    }

    private User loadAndSaveUser() {
        User user = new User();
        user.setName("Jorge");
        user.setEmail("jorge@gmail.com");

        user.setPassword(passwordEncoder.encode("jorge123"));
        return userRepository.save(user);
    }

    private Map<String, Exercise> loadAndSaveExercises() {
        List<Exercise> exercises = List.of(
                new Exercise("Supino Reto", "Principal exercício para peitoral.", MuscleGroup.CHEST, "Barra",
                        DifficultyLevel.INTERMEDIATE, "assets/exercises/supinoBarra.png"),
                new Exercise("Agachamento Livre", "Exercício fundamental para pernas e glúteos.", MuscleGroup.LEGS,
                        "Barra", DifficultyLevel.ADVANCED, "assets/exercises/agachamentoLivre.png"),
                new Exercise("Remada Curvada", "Excelente para espessura das costas.", MuscleGroup.BACK, "Barra",
                        DifficultyLevel.INTERMEDIATE, "assets/exercises/remadaCurvada.png"),
                new Exercise("Desenvolvimento Militar", "Trabalha a porção frontal e medial dos ombros.",
                        MuscleGroup.SHOULDERS, "Halteres", DifficultyLevel.INTERMEDIATE,
                        "assets/exercises/desenvolvimentoMilitar.png"),
                new Exercise("Rosca Direta", "Focado no trabalho do bíceps braquial.", MuscleGroup.BICEPS, "Barra",
                        DifficultyLevel.BEGINNER, "assets/exercises/roscaDireta.png"),
                new Exercise("Tríceps Pulley", "Isolamento eficaz para o tríceps.", MuscleGroup.TRICEPS, "Polia",
                        DifficultyLevel.BEGINNER, "assets/exercises/tricepsPulley.png"),
                new Exercise("Levantamento Terra", "Trabalha a cadeia posterior completa", MuscleGroup.BACK, "Barra",
                        DifficultyLevel.ADVANCED, null),
                new Exercise("Crucifixo", "Exercício essencial para desenvolvimento da parte interna do peito",
                        MuscleGroup.CHEST, "Nenhum", DifficultyLevel.BEGINNER, null));

        List<Exercise> savedExercises = exerciseRepository.saveAll(exercises);

        return savedExercises.stream()
                .collect(Collectors.toMap(Exercise::getName, Function.identity()));
    }

    private void loadWorkouts(User user, Map<String, Exercise> exercisesByName) {

        Workout pushWorkout = new Workout(
                "Push Day",
                "Foco em peito, ombro e tríceps",
                user,
                DifficultyLevel.INTERMEDIATE);
        pushWorkout.setWorkoutType(WorkoutType.PUSH);
        pushWorkout.addExercise(exercisesByName.get("Supino Reto"), 3, 8, 80.0, 90, 1);
        pushWorkout.addExercise(exercisesByName.get("Desenvolvimento Militar"), 3, 8, 70.0, 90, 2);
        pushWorkout.addExercise(exercisesByName.get("Rosca Direta"), 3, 10, 20.0, 60, 3);

        Workout pullWorkout = new Workout(
                "Pull Day",
                "Foco em costas e bíceps",
                user,
                DifficultyLevel.INTERMEDIATE);
        pushWorkout.setWorkoutType(WorkoutType.PULL);
        pullWorkout.addExercise(exercisesByName.get("Remada Curvada"), 4, 12, 60.0, 75, 1);
        pullWorkout.addExercise(exercisesByName.get("Levantamento Terra"), 3, 15, 20.0, 45, 2);
        pullWorkout.addExercise(exercisesByName.get("Rosca Direta"), 3, 15, 15.0, 45, 3);

        Workout legsWorkout = new Workout(
                "Leg Day",
                "Foco em pernas",
                user,
                DifficultyLevel.ADVANCED);
        legsWorkout.setWorkoutType(WorkoutType.LEGS);
        legsWorkout.addExercise(exercisesByName.get("Agachamento Livre"), 5, 10, 90.0, 120, 1);

        Workout fullBodyWorkout = new Workout(
                "Full Body - Iniciante",
                "Treino de corpo inteiro para adaptação",
                user,
                DifficultyLevel.BEGINNER);
        fullBodyWorkout.setWorkoutType(WorkoutType.FULL_BODY);
        fullBodyWorkout.addExercise(exercisesByName.get("Agachamento Livre"), 3, 12, 40.0, 90, 1);
        fullBodyWorkout.addExercise(exercisesByName.get("Crucifixo"), 3, 15, 0.0, 60, 2);
        fullBodyWorkout.addExercise(exercisesByName.get("Remada Curvada"), 3, 12, 40.0, 90, 3);

        workoutRepository.saveAll(List.of(pushWorkout, pullWorkout, legsWorkout, fullBodyWorkout));
        ;
    }

    private void loadHistory(User user, Map<String, Exercise> exercisesByName) {
        System.out.println(">>> Gerando histórico de treinos e gamificação...");

        // Criar um log de treino concluído ontem (para não quebrar Streak se hoje for
        // dia vazio)
        WorkoutLog historicalLog = new WorkoutLog();
        historicalLog.setUser(user);
        historicalLog.setWorkoutName("Primeiro Treino Teste");
        historicalLog.setStatus(WorkoutLogStatus.COMPLETED);
        historicalLog.setStartedAt(LocalDateTime.now().minusDays(1).minusHours(1));
        historicalLog.setCompletedAt(LocalDateTime.now().minusDays(1));

        // Adicionar Exercícios e Sets (Usa nova modelagem 1NF)
        ExerciseLog benchPressLog = new ExerciseLog();
        benchPressLog.setExercise(exercisesByName.get("Supino Reto"));
        benchPressLog.setExerciseName("Supino Reto");
        benchPressLog.setWorkoutLog(historicalLog);
        benchPressLog.addSet(10, 60.0);
        benchPressLog.addSet(8, 65.0);
        benchPressLog.addSet(8, 70.0);

        historicalLog.addExerciseLog(benchPressLog);

        workoutLogRepository.save(historicalLog);

        // Disparar Gamificação manualmente para esse log
        gamificationService.awardXpForWorkout(user, historicalLog);

        userRepository.save(user); // Gravar o XP ganho
    }
}
