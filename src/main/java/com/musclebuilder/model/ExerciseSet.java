package com.musclebuilder.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "exercise_sets")
public class ExerciseSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer reps;

    @Column(nullable = false)
    private Double weight;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_log_id", nullable = false)
    private ExerciseLog exerciseLog;

    public ExerciseSet() {}

    public ExerciseSet(Integer reps, Double weight, Integer orderIndex, ExerciseLog exerciseLog) {
        this.reps = reps;
        this.weight = weight;
        this.orderIndex = orderIndex;
        this.exerciseLog = exerciseLog;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public ExerciseLog getExerciseLog() {
        return exerciseLog;
    }

    public void setExerciseLog(ExerciseLog exerciseLog) {
        this.exerciseLog = exerciseLog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExerciseSet that = (ExerciseSet) o;
        // Se ambos têm ID, são iguais se o ID for igual
        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }
        // Senão, comparamos pelos campos de negócio (Log + Ordem)
        return Objects.equals(orderIndex, that.orderIndex) &&
                Objects.equals(exerciseLog, that.exerciseLog);
    }

    @Override
    public int hashCode() {
        // Padrão seguro para JPA para evitar problemas com Lazy Loading
        return getClass().hashCode();
    }

}
