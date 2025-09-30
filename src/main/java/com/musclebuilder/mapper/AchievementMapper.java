package com.musclebuilder.mapper;

import com.musclebuilder.dto.AchievementDTO;
import com.musclebuilder.model.Achievement;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AchievementMapper {

    AchievementDTO toDto(Achievement achievement);

}
