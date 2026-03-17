package com.musclebuilder.mapper;

import com.musclebuilder.dto.WorkoutLogResponseDTO;
import com.musclebuilder.model.ExerciseLog;
import com.musclebuilder.model.ExerciseSet;
import com.musclebuilder.model.WorkoutLog;
import com.musclebuilder.model.WorkoutLogStatus;
import com.musclebuilder.repository.ExerciseLogRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class WorkoutLogMapper {

    @Autowired
    protected ExerciseLogRepository exerciseLogRepository;

    public abstract WorkoutLogResponseDTO toDto(WorkoutLog workoutLog);

    public abstract WorkoutLogResponseDTO.ExerciseSetResponseDTO toSetDto(ExerciseSet entity);

    @Mapping(source = "exerciseSets", target = "sets")
    @Mapping(target = "targetSets", expression = "java(findTargetSets(entity))")
    @Mapping(target = "targetReps", expression = "java(findTargetReps(entity))")
    @Mapping(target = "lastPerformance", expression = "java(findLastPerformance(entity))")
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

    protected WorkoutLogResponseDTO.LastPerformanceDTO findLastPerformance(ExerciseLog entity) {
        if (entity.getWorkoutLog() == null || entity.getWorkoutLog().getUser() == null || entity.getExercise() == null) {
            return null;
        }

        return exerciseLogRepository
                .findFirstByWorkoutLog_UserAndExerciseAndWorkoutLog_StatusOrderByWorkoutLog_CompletedAtDesc(
                        entity.getWorkoutLog().getUser(),
                        entity.getExercise(),
                        WorkoutLogStatus.COMPLETED)
                .filter(lastLog -> !lastLog.getId().equals(entity.getId()))
                .map(lastLog -> new WorkoutLogResponseDTO.LastPerformanceDTO(
                        lastLog.getMaxWeight(),
                        lastLog.getTotalReps(),
                        lastLog.getWorkoutLog().getCompletedAt() != null
                                ? lastLog.getWorkoutLog().getCompletedAt().toLocalDate()
                                : null))
                .orElse(null);
    }

}
