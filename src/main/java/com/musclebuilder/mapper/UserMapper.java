package com.musclebuilder.mapper;

import com.musclebuilder.dto.UserDTO;
import com.musclebuilder.dto.UserRegistrationDTO;
import com.musclebuilder.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDto(User user);

    User toEntity(UserRegistrationDTO userRegistrationDTO);
}
