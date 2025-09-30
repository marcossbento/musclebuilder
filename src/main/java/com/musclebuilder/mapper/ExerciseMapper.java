package com.musclebuilder.mapper;

import com.musclebuilder.dto.ExerciseDTO;
import com.musclebuilder.model.Exercise;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    ExerciseDTO toDto(Exercise exercise);

    Exercise toEntity(ExerciseDTO exerciseDTO);
}
