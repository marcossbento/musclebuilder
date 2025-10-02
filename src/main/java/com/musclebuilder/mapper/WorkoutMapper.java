package com.musclebuilder.mapper;

import com.musclebuilder.dto.WorkoutCreateDTO;
import com.musclebuilder.dto.WorkoutResponseDTO;
import com.musclebuilder.dto.WorkoutUpdateDTO;
import com.musclebuilder.model.Workout;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {WorkoutExerciseMapper.class})
public interface WorkoutMapper {

    @Mapping(source = "workoutExercises", target = "exercises")
    WorkoutResponseDTO toDto(Workout workout);

    Workout toEntity(WorkoutCreateDTO workoutCreateDTO);

    Workout toEntity(WorkoutUpdateDTO workoutUpdateDTO);

}
