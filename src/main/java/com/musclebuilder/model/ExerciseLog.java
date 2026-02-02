package com.musclebuilder.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private Integer setsCompleted = 0;

    @OneToMany(mappedBy = "exerciseLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseSet> exerciseSets = new ArrayList<>();

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

    public ExerciseLog() {
    }

    public ExerciseLog(Long id, WorkoutLog workoutLog, Exercise exercise, String exerciseName,
            Integer setsCompleted, List<ExerciseSet> exerciseSets, Double weightUsed,
            Integer restSeconds, Double volume, Double maxWeight, Integer totalReps,
            Integer orderPosition, String notes, Integer difficultyRating,
            LocalDateTime startedAt, LocalDateTime completedAt,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.workoutLog = workoutLog;
        this.exercise = exercise;
        this.exerciseName = exerciseName;
        this.setsCompleted = setsCompleted;
        this.exerciseSets = exerciseSets != null ? exerciseSets : new ArrayList<>();
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

    private void calculateMetrics() {
        if (exerciseSets != null && !exerciseSets.isEmpty()) {
            this.totalReps = exerciseSets.stream()
                    .mapToInt(ExerciseSet::getReps)
                    .sum();

            this.volume = exerciseSets.stream()
                    .mapToDouble(set -> set.getReps() * (set.getWeight() != null ? set.getWeight() : 0.0))
                    .sum();

            this.maxWeight = exerciseSets.stream()
                    .mapToDouble(set -> set.getWeight() != null ? set.getWeight() : 0.0)
                    .max()
                    .orElse(0.0);
        }
    }

    public void addSet(int reps, Double weight) {
        if (this.exerciseSets == null) {
            this.exerciseSets = new ArrayList<>();
        }

        ExerciseSet newSet = new ExerciseSet();
        newSet.setReps(reps);
        newSet.setWeight(weight);
        newSet.setOrderIndex(this.exerciseSets.size() + 1);
        newSet.setExerciseLog(this);

        this.exerciseSets.add(newSet);
        this.setsCompleted = this.exerciseSets.size(); // Keep legacy field in sync

        calculateMetrics();
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

    public List<ExerciseSet> getExerciseSets() {
        return exerciseSets;
    }

    public void setExerciseSets(List<ExerciseSet> exerciseSets) {
        this.exerciseSets = exerciseSets;
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
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ExerciseLog that = (ExerciseLog) o;
        if (id != null && that.id != null)
            return Objects.equals(id, that.id);
        return Objects.equals(orderPosition, that.orderPosition) &&
                Objects.equals(exercise, that.exercise) &&
                Objects.equals(workoutLog, that.workoutLog);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ExerciseLog{" +
                "id=" + id +
                ", exerciseName='" + exerciseName + '\'' +
                ", totalReps=" + totalReps +
                ", volume=" + volume +
                '}';
    }

}
