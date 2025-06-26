package com.musclebuilder.model;

public enum WorkoutLogStatus {
    IN_PROGRESS("Em progresso"),
    COMPLETED("Concluído"),
    CANCELLED("Cancelado");

    private final String description;

    WorkoutLogStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
