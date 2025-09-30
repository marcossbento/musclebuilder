package com.musclebuilder.mapper;

import com.musclebuilder.dto.ExerciseDTO;
import com.musclebuilder.model.Exercise;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    ExerciseMapper INSTANCE = Mappers.getMapper(ExerciseMapper.class);

    ExerciseDTO toDto(Exercise exercise);

    Exercise toEntity(ExerciseDTO exerciseDTO);
}
