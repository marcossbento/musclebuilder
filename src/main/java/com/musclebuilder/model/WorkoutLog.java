package com.musclebuilder.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "workout_logs")
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

    @OneToMany(mappedBy = "workoutLog", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ExerciseLog> exerciseLogs = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public WorkoutLog() {}

    public WorkoutLog(final Long id, final User user, final Workout workout, final String workoutName, final LocalDateTime startedAt, final LocalDateTime completedAt, final Integer durationMinutes, final Double totalVolume, final String notes, final WorkoutLogStatus status, final List<ExerciseLog> exerciseLogs, final LocalDateTime createdAt, final LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.workout = workout;
        this.workoutName = workoutName;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.durationMinutes = durationMinutes;
        this.totalVolume = totalVolume;
        this.notes = notes;
        this.status = status;
        this.exerciseLogs = exerciseLogs;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Workout getWorkout() {
        return this.workout;
    }

    public void setWorkout(final Workout workout) {
        this.workout = workout;
    }

    public String getWorkoutName() {
        return this.workoutName;
    }

    public void setWorkoutName(final String workoutName) {
        this.workoutName = workoutName;
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

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    public void setDurationMinutes(final Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getTotalVolume() {
        return this.totalVolume;
    }

    public void setTotalVolume(final Double totalVolume) {
        this.totalVolume = totalVolume;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(final String notes) {
        this.notes = notes;
    }

    public WorkoutLogStatus getStatus() {
        return this.status;
    }

    public void setStatus(final WorkoutLogStatus status) {
        this.status = status;
    }

    public List<ExerciseLog> getExerciseLogs() {
        return this.exerciseLogs;
    }

    public void setExerciseLogs(final List<ExerciseLog> exerciseLogs) {
        this.exerciseLogs = exerciseLogs;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    //Métodos auxiliares para configuração do relacionamento bidirecional
    public void addExerciseLog(ExerciseLog exerciseLog) {
        exerciseLogs.add(exerciseLog);
        exerciseLog.setWorkoutLog(this); //Configura ambos os lados
    }

    public void removeExerciseLog(ExerciseLog exerciseLog) {
        exerciseLogs.remove(exerciseLog);
        exerciseLog.setWorkoutLog(null); //Remove ambos os lados
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final WorkoutLog that = (WorkoutLog) o;

        // Se ambos têm ID, use o ID
        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }

        // Caso contrário, use a combinação única de negócio
        // Um usuário não pode iniciar dois treinos no exato mesmo momento
        return Objects.equals(user != null ? user.getId() : null,
                that.user != null ? that.user.getId() : null) &&
                Objects.equals(startedAt, that.startedAt) &&
                Objects.equals(workoutName, that.workoutName);
    }

    @Override
    public int hashCode() {
        // Se tem ID, use o ID
        if (id != null) {
            return Objects.hash(id);
        }

        // Caso contrário, use a combinação única de negócio
        return Objects.hash(
                user != null ? user.getId() : null,
                startedAt,
                workoutName
        );
    }

    @Override
    public String toString() {
        return "WorkoutLog{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", workoutId=" + (workout != null ? workout.getId() : null) +
                ", workoutName='" + workoutName + '\'' +
                ", status=" + status +
                ", startedAt=" + startedAt +
                ", completedAt=" + completedAt +
                ", durationMinutes=" + durationMinutes +
                ", totalVolume=" + totalVolume +
                ", exerciseCount=" + (exerciseLogs != null ? exerciseLogs.size() : 0) +
                ", createdAt=" + createdAt +
                '}';
    }

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
