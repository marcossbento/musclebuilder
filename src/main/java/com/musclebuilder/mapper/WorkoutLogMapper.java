package com.musclebuilder.mapper;

import com.musclebuilder.dto.WorkoutLogResponseDTO;
import com.musclebuilder.model.ExerciseLog;
import com.musclebuilder.model.ExerciseSet;
import com.musclebuilder.model.WorkoutLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkoutLogMapper {

    WorkoutLogResponseDTO toDto(WorkoutLog workoutLog);

    WorkoutLogResponseDTO.ExerciseSetResponseDTO toSetDto(ExerciseSet entity);

    @Mapping(source = "exerciseSets", target = "sets")
    @Mapping(target = "targetSets", ignore = true)
    @Mapping(target = "targetReps", ignore = true)
    WorkoutLogResponseDTO.ExerciseLogResponseDTO toExerciseLogDto(ExerciseLog entity);

}
