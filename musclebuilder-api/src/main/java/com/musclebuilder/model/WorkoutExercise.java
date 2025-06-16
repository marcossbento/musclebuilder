package com.musclebuilder.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "workout_exercises")
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(nullable = false)
    private Integer sets;

    @Column(name = "reps_per_set", nullable = false)
    private Integer repsPerSet;

    private Double weight;

    @Column(name = "rest_seconds")
    private Integer restSeconds;

    @Column(name = "order_position")
    private Integer orderPosition;

    public WorkoutExercise() {}

    public WorkoutExercise(final Long id, final Workout workout, final Exercise exercise, final Integer sets, final Integer repsPerSet, final Double weight, final Integer restSeconds, final Integer orderPosition) {
        this.id = id;
        this.workout = workout;
        this.exercise = exercise;
        this.sets = sets;
        this.repsPerSet = repsPerSet;
        this.weight = weight;
        this.restSeconds = restSeconds;
        this.orderPosition = orderPosition;
    }

    // IDless constructor for easier creation
    public WorkoutExercise(Workout workout, Exercise exercise, Integer sets, Integer repsPerSet, Double weight, Integer restSeconds, Integer orderPosition) {
        this.workout = workout;
        this.exercise = exercise;
        this.sets = sets;
        this.repsPerSet = repsPerSet;
        this.weight = weight;
        this.restSeconds = restSeconds;
        this.orderPosition = orderPosition;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Workout getWorkout() {
        return this.workout;
    }

    public void setWorkout(final Workout workout) {
        this.workout = workout;
    }

    public Exercise getExercise() {
        return this.exercise;
    }

    public void setExercise(final Exercise exercise) {
        this.exercise = exercise;
    }

    public Integer getSets() {
        return this.sets;
    }

    public void setSets(final Integer sets) {
        this.sets = sets;
    }

    public Integer getRepsPerSet() {
        return this.repsPerSet;
    }

    public void setRepsPerSet(final Integer repsPerSet) {
        this.repsPerSet = repsPerSet;
    }

    public Double getWeight() {
        return this.weight;
    }

    public void setWeight(final Double weight) {
        this.weight = weight;
    }

    public Integer getRestSeconds() {
        return this.restSeconds;
    }

    public void setRestSeconds(final Integer restSeconds) {
        this.restSeconds = restSeconds;
    }

    public Integer getOrderPosition() {
        return this.orderPosition;
    }

    public void setOrderPosition(final Integer orderPosition) {
        this.orderPosition = orderPosition;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final WorkoutExercise that = (WorkoutExercise) o;

        //Se ambos têm ID, use o ID
        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }

        //Se não, use a combinação única de negócio
        return Objects.equals(workout != null ? workout.getId() : null,
                that.workout != null ? that.workout.getId() : null) &&
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
                workout != null ? workout.getId() : null,
                exercise != null ? exercise.getId() : null,
                orderPosition
        );
    }

    @Override
    public String toString() {
        return "WorkoutExercise{" +
                "id=" + id +
                ", workoutId=" + (workout != null ? workout.getId() : null) +
                ", exerciseId=" + (exercise != null ? exercise.getId() : null) +
                ", exerciseName='" + (exercise != null ? exercise.getName() : null) + '\'' +
                ", sets=" + sets +
                ", repsPerSet=" + repsPerSet +
                ", weight=" + weight +
                ", restSeconds=" + restSeconds +
                ", orderPosition=" + orderPosition +
                '}';
    }
}
