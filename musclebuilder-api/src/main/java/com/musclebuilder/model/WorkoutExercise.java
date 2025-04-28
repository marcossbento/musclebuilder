package com.musclebuilder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workout_exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
