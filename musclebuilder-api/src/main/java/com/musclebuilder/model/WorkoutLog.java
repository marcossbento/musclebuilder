package com.musclebuilder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id")
    private Workout workout;

    @Column(name = "workout_name", nullable = false)
    private String workoutName;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    //Soma do peso X reps de todos os exercícios
    @Column(name = "total_volume")
    private Double totalVolume;

    @Column(name = "notes")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WorkoutLogStatus status = WorkoutLogStatus.IN_PROGRESS;

    @OneToMany(mappedBy = "workoutLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseLog> exerciseLogs = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    //Métodos auxiliares para cálculos
    public void calculateTotalVolume() {
        this.totalVolume = exerciseLogs.stream()
                .mapToDouble(ExerciseLog::getVolume)
                .sum();
    }

    public void completeWorkout() {
        this.completedAt = LocalDateTime.now();
        this.status = WorkoutLogStatus.COMPLETED;
        if (startedAt != null && completedAt != null) {
            this.durationMinutes = (int) java.time.Duration.between(startedAt, completedAt).toMinutes();
        }
        calculateTotalVolume();
    }
}
