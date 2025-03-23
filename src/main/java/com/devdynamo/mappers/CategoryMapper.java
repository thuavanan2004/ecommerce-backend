package com.devdynamo.mappers;

import com.devdynamo.dtos.request.CategoryRequestDTO;
import com.devdynamo.dtos.response.CategoryResponseDTO;
import com.devdynamo.dtos.response.ProductResponseDTO;
import com.devdynamo.entities.CategoryEntity;
import com.devdynamo.entities.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponseDTO toDTO(CategoryEntity category);

    CategoryEntity toEntity(CategoryRequestDTO dto);
}
