package com.musclebuilder.mapper;

import com.musclebuilder.dto.AchievementDTO;
import com.musclebuilder.model.Achievement;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AchievementMapper {

    AchievementMapper INSTANCE = Mappers.getMapper(AchievementMapper.class);

    AchievementDTO toDto(Achievement achievement);

    Achievement toEntity(AchievementDTO achievementDTO);
}
