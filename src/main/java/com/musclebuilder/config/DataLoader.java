package com.musclebuilder.config;

import com.musclebuilder.model.DifficultyLevel;
import com.musclebuilder.model.Exercise;
import com.musclebuilder.model.User;
import com.musclebuilder.model.Workout;
import com.musclebuilder.repository.ExerciseRepository;
import com.musclebuilder.repository.UserRepository;
import com.musclebuilder.repository.WorkoutRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutRepository workoutRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, ExerciseRepository exerciseRepository, WorkoutRepository workoutRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutRepository = workoutRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println(">>> Povoando banco de dados com  dados iniciais...");
            User savedUser = loadAndSaveUser();

            List<Exercise> savedExercises = loadAndSaveExercises();

            loadWorkouts(savedUser, savedExercises);

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

    private List<Exercise> loadAndSaveExercises() {
        List<Exercise> exercises = List.of(
                new Exercise("Supino Reto", "Principal exercício para peitoral.", "PEITO", "Barra", DifficultyLevel.INTERMEDIATE, "assets/exercises/supinoBarra.png"),
                new Exercise("Agachamento Livre", "Exercício fundamental para pernas e glúteos.", "PERNAS", "Barra", DifficultyLevel.ADVANCED, "assets/exercises/agachamentoLivre.png"),
                new Exercise("Remada Curvada", "Excelente para espessura das costas.", "COSTAS", "Barra", DifficultyLevel.INTERMEDIATE, "assets/exercises/remadaCurvada.png"),
                new Exercise("Desenvolvimento Militar", "Trabalha a porção frontal e medial dos ombros.", "OMBROS", "Halteres", DifficultyLevel.INTERMEDIATE, "assets/exercises/desenvolvimentoMilitar.png"),
                new Exercise("Rosca Direta", "Focado no trabalho do bíceps braquial.", "BICEPS", "Barra", DifficultyLevel.BEGINNER, "assets/exercises/roscaDireta.png"),
                new Exercise("Tríceps Pulley", "Isolamento eficaz para o tríceps.", "TRICEPS", "Polia", DifficultyLevel.BEGINNER, "assets/exercises/tricepsPulley.png")
        );

        return exerciseRepository.saveAll(exercises);
    }

    private void loadWorkouts(User user, List<Exercise> exercises) {
        Exercise supino = exercises.get(0);
        Exercise agachamento = exercises.get(1);
        Exercise remada = exercises.get(2);
        Exercise desenvolvimento = exercises.get(3);
        Exercise roscaDireta = exercises.get(4);
        Exercise tricepsPulley = exercises.get(5);

        Workout treinoForca = new Workout(
                "Push/Pull Padrão",
                "Treino focado em movimentos compostos básico para ganho de força.",
                user,
                DifficultyLevel.INTERMEDIATE
        );
        treinoForca.addExercise(supino, 3, 8, 80.0, 90, 1);
        treinoForca.addExercise(remada, 3, 8, 70.0, 90, 2);
        treinoForca.addExercise(desenvolvimento, 3, 10, 20.0, 60, 3);

        Workout treinoHipertrofia = new Workout(
                "Full Body Hipertrofia",
                "Treino de corpo inteiro para estimular o crescimento muscular",
                user,
                DifficultyLevel.BEGINNER
        );
        treinoHipertrofia.addExercise(agachamento, 4, 12, 60.0, 75, 1);
        treinoHipertrofia.addExercise(roscaDireta, 3, 15, 20.0, 45, 2);
        treinoHipertrofia.addExercise(tricepsPulley, 3, 15, 15.0, 45, 3);

        workoutRepository.saveAll(List.of(treinoForca, treinoHipertrofia));;
    }
}
