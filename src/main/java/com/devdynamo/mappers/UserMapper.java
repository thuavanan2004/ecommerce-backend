package com.devdynamo.mappers;


import com.devdynamo.dtos.request.UserRequestDTO;
import com.devdynamo.dtos.response.UserResponseDTO;
import com.devdynamo.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toDTO(UserEntity user);

    UserEntity toEntity(UserRequestDTO dto);

    void updateUserFromDTO(UserRequestDTO dto, @MappingTarget UserEntity entity);
}