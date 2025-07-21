package com.musclebuilder.config;

import com.musclebuilder.model.DifficultyLevel;
import com.musclebuilder.model.Exercise;
import com.musclebuilder.model.User;
import com.musclebuilder.repository.ExerciseRepository;
import com.musclebuilder.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.musclebuilder.model.DifficultyLevel.*;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, ExerciseRepository exerciseRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println(">>> Povoando banco de dados com  dados iniciais...");
            loadUsers();
            loadExercises();
            System.out.println(">>> Povoamento concluído");
        } else {
            System.out.println(">>> O banco de dados já contem dados. Povoamento cancelado");
        }
    }

    private void loadUsers() {
        User user = new User();
        user.setName("Jorge");
        user.setEmail("jorge@gmail.com");

        user.setPassword(passwordEncoder.encode("jorge123"));
        userRepository.save(user);
    }

    private void loadExercises() {
        List<Exercise> exercises = List.of(
                new Exercise("Supino Reto", "Principal exercício para peitoral.", "PEITO", "Barra", DifficultyLevel.INTERMEDIATE, "assets/exercises/supinoBarra_exercise.png"),
                new Exercise("Agachamento Livre", "Exercício fundamental para pernas e glúteos.", "PERNAS", "Barra", DifficultyLevel.ADVANCED, "https://i.imgur.com/sde3d5s.png"),
                new Exercise("Remada Curvada", "Excelente para espessura das costas.", "COSTAS", "Barra", DifficultyLevel.INTERMEDIATE, "https://i.imgur.com/d5b5b4s.png"),
                new Exercise("Desenvolvimento Militar", "Trabalha a porção frontal e medial dos ombros.", "OMBROS", "Halteres", DifficultyLevel.INTERMEDIATE, "https://i.imgur.com/fGg5H4A.png"),
                new Exercise("Rosca Direta", "Focado no trabalho do bíceps braquial.", "BICEPS", "Barra", DifficultyLevel.BEGINNER, "https://i.imgur.com/s4a3d5s.png"),
                new Exercise("Tríceps Pulley", "Isolamento eficaz para o tríceps.", "TRICEPS", "Polia", DifficultyLevel.BEGINNER, "https://i.imgur.com/g5g3H4A.png")
        );

        exerciseRepository.saveAll(exercises);
    }
}
