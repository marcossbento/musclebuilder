package com.musclebuilder.mapper;

import com.musclebuilder.dto.WorkoutLogResponseDTO;
import com.musclebuilder.model.ExerciseLog;
import com.musclebuilder.model.ExerciseSet;
import com.musclebuilder.model.WorkoutLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class WorkoutLogMapper {

    public abstract WorkoutLogResponseDTO toDto(WorkoutLog workoutLog);

    public abstract WorkoutLogResponseDTO.ExerciseSetResponseDTO toSetDto(ExerciseSet entity);

    @Mapping(source = "exerciseSets", target = "sets")
    @Mapping(target = "targetSets", expression = "java(findTargetSets(entity))")
    @Mapping(target = "targetReps", expression = "java(findTargetReps(entity))")
    public abstract WorkoutLogResponseDTO.ExerciseLogResponseDTO toExerciseLogDto(ExerciseLog entity);

    protected Integer findTargetSets(ExerciseLog entity) {
        if (entity.getWorkoutLog() == null || entity.getWorkoutLog().getWorkout() == null) {
            return null;
        }
        return entity.getWorkoutLog().getWorkout().getWorkoutExercises().stream()
                .filter(we -> we.getExercise().equals(entity.getExercise()))
                .findFirst()
                .map(com.musclebuilder.model.WorkoutExercise::getSets)
                .orElse(null);
    }

    protected Integer findTargetReps(ExerciseLog entity) {
        if (entity.getWorkoutLog() == null || entity.getWorkoutLog().getWorkout() == null) {
            return null;
        }
        return entity.getWorkoutLog().getWorkout().getWorkoutExercises().stream()
                .filter(we -> we.getExercise().equals(entity.getExercise()))
                .findFirst()
                .map(com.musclebuilder.model.WorkoutExercise::getRepsPerSet)
                .orElse(null);
    }

}
