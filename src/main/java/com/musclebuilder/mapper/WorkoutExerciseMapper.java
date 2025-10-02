package com.musclebuilder.mapper;

import com.musclebuilder.dto.WorkoutResponseDTO;
import com.musclebuilder.model.WorkoutExercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkoutExerciseMapper {

    @Mapping(source = "exercise.id", target = "exerciseId")
    @Mapping(source = "exercise.name", target = "exerciseName")
    WorkoutResponseDTO.WorkoutExerciseDTO toDto(WorkoutExercise workoutExercise);

}
