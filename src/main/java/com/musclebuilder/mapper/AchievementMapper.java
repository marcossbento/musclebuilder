package com.musclebuilder.mapper;

import com.musclebuilder.dto.AchievementDTO;
import com.musclebuilder.model.Achievement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AchievementMapper {

    AchievementDTO toDto(Achievement achievement);

}
