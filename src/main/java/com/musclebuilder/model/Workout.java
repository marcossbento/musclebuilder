package com.musclebuilder.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "workouts")
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

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutLog> workoutLogs = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Workout() {}

    public Workout(String name, String description, User user, DifficultyLevel difficultyLevel) {
        this.name = name;
        this.description = description;
        this.user = user;
        this.difficultyLevel = difficultyLevel;
    }

    public Workout(final Long id, final String name, final String description, final String workoutType, final User user, final Integer weekNumber, final Integer dayNumber, final WorkoutStatus status, final Integer estimatedDurationMinutes, final DifficultyLevel difficultyLevel, final List<WorkoutExercise> workoutExercises, final List<WorkoutLog> workoutLogs, final LocalDateTime createdAt, final LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.workoutType = workoutType;
        this.user = user;
        this.weekNumber = weekNumber;
        this.dayNumber = dayNumber;
        this.status = status;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.difficultyLevel = difficultyLevel;
        this.workoutExercises = workoutExercises;
        this.workoutLogs = workoutLogs;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getWorkoutType() {
        return this.workoutType;
    }

    public void setWorkoutType(final String workoutType) {
        this.workoutType = workoutType;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Integer getWeekNumber() {
        return this.weekNumber;
    }

    public void setWeekNumber(final Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public Integer getDayNumber() {
        return this.dayNumber;
    }

    public void setDayNumber(final Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public WorkoutStatus getStatus() {
        return this.status;
    }

    public void setStatus(final WorkoutStatus status) {
        this.status = status;
    }

    public Integer getEstimatedDurationMinutes() {
        return this.estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(final Integer estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public DifficultyLevel getDifficultyLevel() {
        return this.difficultyLevel;
    }

    public void setDifficultyLevel(final DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public List<WorkoutExercise> getWorkoutExercises() {
        return this.workoutExercises;
    }

    public void setWorkoutExercises(final List<WorkoutExercise> workoutExercises) {
        this.workoutExercises = workoutExercises;
    }

    public List<WorkoutLog> getWorkoutLogs() {
        return workoutLogs;
    }

    public void setWorkoutLogs(final List<WorkoutLog> workoutLogs) {
        this.workoutLogs = workoutLogs;
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
        final Workout workout = (Workout) o;
        return Objects.equals(id, workout.id) &&
                Objects.equals(name, workout.name) &&
                Objects.equals(user.getId(), workout.user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, user != null ? user.getId() : null);
    }

    @Override
    public String toString() {
        return "Workout{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", workoutType='" + workoutType + '\'' +
                ", userId=" + (user != null ? user.getId() : null) + // Apenas ID do user
                ", weekNumber=" + weekNumber +
                ", dayNumber=" + dayNumber +
                ", status=" + status +
                ", estimatedDurationMinutes=" + estimatedDurationMinutes +
                ", difficultyLevel=" + difficultyLevel +
                ", exerciseCount=" + (workoutExercises != null ? workoutExercises.size() : 0) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

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
