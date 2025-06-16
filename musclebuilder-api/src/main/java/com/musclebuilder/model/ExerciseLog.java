package com.musclebuilder.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "exercise_logs")
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

    public ExerciseLog() {}

    public ExerciseLog(final Long id, final WorkoutLog workoutLog, final Exercise exercise, final String exerciseName, final Integer setsCompleted, final String repsPerSet, final Double weightUsed, final Integer restSeconds, final Double volume, final Double maxWeight, final Integer totalReps, final Integer orderPosition, final String notes, final Integer difficultyRating, final LocalDateTime startedAt, final LocalDateTime completedAt, final LocalDateTime createdAt, final LocalDateTime updatedAt) {
        this.id = id;
        this.workoutLog = workoutLog;
        this.exercise = exercise;
        this.exerciseName = exerciseName;
        this.setsCompleted = setsCompleted;
        this.repsPerSet = repsPerSet;
        this.weightUsed = weightUsed;
        this.restSeconds = restSeconds;
        this.volume = volume;
        this.maxWeight = maxWeight;
        this.totalReps = totalReps;
        this.orderPosition = orderPosition;
        this.notes = notes;
        this.difficultyRating = difficultyRating;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public WorkoutLog getWorkoutLog() {
        return this.workoutLog;
    }

    public void setWorkoutLog(final WorkoutLog workoutLog) {
        this.workoutLog = workoutLog;
    }

    public Exercise getExercise() {
        return this.exercise;
    }

    public void setExercise(final Exercise exercise) {
        this.exercise = exercise;
    }

    public String getExerciseName() {
        return this.exerciseName;
    }

    public void setExerciseName(final String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public Integer getSetsCompleted() {
        return this.setsCompleted;
    }

    public void setSetsCompleted(final Integer setsCompleted) {
        this.setsCompleted = setsCompleted;
    }

    public String getRepsPerSet() {
        return this.repsPerSet;
    }

    public void setRepsPerSet(final String repsPerSet) {
        this.repsPerSet = repsPerSet;
    }

    public Double getWeightUsed() {
        return this.weightUsed;
    }

    public void setWeightUsed(final Double weightUsed) {
        this.weightUsed = weightUsed;
    }

    public Integer getRestSeconds() {
        return this.restSeconds;
    }

    public void setRestSeconds(final Integer restSeconds) {
        this.restSeconds = restSeconds;
    }

    public Double getVolume() {
        return this.volume;
    }

    public void setVolume(final Double volume) {
        this.volume = volume;
    }

    public Double getMaxWeight() {
        return this.maxWeight;
    }

    public void setMaxWeight(final Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public Integer getTotalReps() {
        return this.totalReps;
    }

    public void setTotalReps(final Integer totalReps) {
        this.totalReps = totalReps;
    }

    public Integer getOrderPosition() {
        return this.orderPosition;
    }

    public void setOrderPosition(final Integer orderPosition) {
        this.orderPosition = orderPosition;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(final String notes) {
        this.notes = notes;
    }

    public Integer getDifficultyRating() {
        return this.difficultyRating;
    }

    public void setDifficultyRating(final Integer difficultyRating) {
        this.difficultyRating = difficultyRating;
    }

    public LocalDateTime getStartedAt() {
        return this.startedAt;
    }

    public void setStartedAt(final LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return this.completedAt;
    }

    public void setCompletedAt(final LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ExerciseLog that = (ExerciseLog) o;

        // Se ambos têm ID, use o ID
        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }

        // Caso contrário, use a combinação única de negócio
        return Objects.equals(workoutLog != null ? workoutLog.getId() : null,
                        that.workoutLog != null ? that.workoutLog.getId() : null) &&
                Objects.equals(exercise != null ? exercise.getId() : null,
                        that.exercise != null ? that.exercise.getId() : null) &&
                Objects.equals(orderPosition, that.orderPosition);
    }

    @Override
    public int hashCode() {

        if (id != null) {
            return Objects.hash(id);
        }

        return Objects.hash(
                workoutLog != null ? workoutLog.getId() : null,
                exercise != null ? exercise.getId() : null,
                orderPosition
        );
    }

    @Override
    public String toString() {
        return "ExerciseLog{" +
                "id=" + id +
                ", workoutLogId=" + (workoutLog != null ? workoutLog.getId() : null) +
                ", exerciseId=" + (exercise != null ? exercise.getId() : null) +
                ", exerciseName='" + exerciseName + '\'' +
                ", setsCompleted=" + setsCompleted +
                ", repsPerSet='" + repsPerSet + '\'' +
                ", weightUsed=" + weightUsed +
                ", volume=" + volume +
                ", totalReps=" + totalReps +
                ", orderPosition=" + orderPosition +
                ", difficultyRating=" + difficultyRating +
                ", createdAt=" + createdAt +
                '}';
    }

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
