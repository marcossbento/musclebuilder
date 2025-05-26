package com.musclebuilder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exercise_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_log_id", nullable = false)
    private WorkoutLog workoutLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "exercise_name", nullable = false)
    private String exerciseName;

    @Column(name = "sets_completed", nullable = false)
    private Integer setsCompleted;

    @Column(name = "reps_per_set", nullable = false)
    private String repsPerSet;

    @Column(name = "weight_used")
    private Double weightUsed;

    @Column(name = "rest_seconds")
    private Integer restSeconds;

    @Column(name = "volume")
    private Double volume;

    @Column(name = "max_weight")
    private Double maxWeight;

    @Column(name = "total_reps")
    private Integer totalReps;

    @Column(name = "order_position")
    private Integer orderPosition;

    @Column(name = "notes")
    private String notes;

    @Column(name = "difficulty_rating")
    private Integer difficultyRating;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateMetrics();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateMetrics();
    }

    private void calculateMetrics() {
        if (repsPerSet != null && !repsPerSet.isEmpty()) {
            String[] reps = repsPerSet.split(",");
            this.totalReps = 0;
            this.maxWeight = this.weightUsed;

            for (String rep : reps) {
                try {
                    this.totalReps += Integer.parseInt(rep.trim());
                } catch (NumberFormatException e) {

                }
            }
            //Volume = peso X reps totais
            if (weightUsed != null && totalReps != null) {
                this.volume = weightUsed * totalReps;
            }
        }
    }

    public void addSet(int reps, Double weight) {
        if (this.repsPerSet == null || this.repsPerSet.isEmpty()) {
            this.repsPerSet = String.valueOf(reps);
        } else {
            this.repsPerSet += "," + reps;
        }

        if (weight != null) {
            if(this.maxWeight == null || weight > this.maxWeight) {
                this.maxWeight = weight;
            }
            this.weightUsed = weight;
        }

        calculateMetrics();
    }
}
