package com.musclebuilder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workouts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "workout_type")
    private String workoutType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //weekNumber and dayNumber for periodization
    @Column(name = "week_number")
    private Integer weekNumber;

    @Column(name = "day_number")
    private Integer dayNumber;

    @Enumerated(EnumType.STRING)
    private WorkoutStatus status = WorkoutStatus.ACTIVE;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @Column(name = "difficulty_level")
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutExercise> workoutExercises = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addExercise(Exercise exercise, int sets, int repsPerSet, Double weight, int restSeconds, int orderPosition) {
        WorkoutExercise workoutExercise = new WorkoutExercise(this, exercise, sets, repsPerSet, weight, restSeconds, orderPosition);
        workoutExercises.add(workoutExercise);
    }

    public void removeExercise(Exercise exercise) {
        workoutExercises.removeIf(workoutExercise -> workoutExercise.getExercise().equals(exercise));
    }

}
