package com.musclebuilder.mapper;

import com.musclebuilder.dto.WorkoutResponseDTO;
import com.musclebuilder.model.Workout;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {

    WorkoutMapper INSTANCE = Mappers.getMapper(WorkoutMapper.class);

    WorkoutResponseDTO toDto(Workout workout);


}
