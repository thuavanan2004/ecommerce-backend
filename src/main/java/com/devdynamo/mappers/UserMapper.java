package com.devdynamo.mappers;


import com.devdynamo.dtos.response.UserResponseDTO;
import com.devdynamo.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toDTO(UserEntity user);

    UserEntity toEntity(UserResponseDTO dto);
}