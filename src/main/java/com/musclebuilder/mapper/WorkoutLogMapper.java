package com.musclebuilder.mapper;

import com.musclebuilder.dto.WorkoutLogResponseDTO;
import com.musclebuilder.model.WorkoutLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkoutLogMapper {

    WorkoutLogResponseDTO toDto(WorkoutLog workoutLog);
}
