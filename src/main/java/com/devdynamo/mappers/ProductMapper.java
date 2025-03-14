package com.devdynamo.mappers;

import com.devdynamo.dtos.response.ProductResponseDTO;
import com.devdynamo.entities.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mappings({@Mapping(source = "category.id", target = "categoryId"),
            @Mapping(source = "category.name", target = "categoryName")})
    ProductResponseDTO toDTO(ProductEntity product);

    ProductEntity toEntity(ProductResponseDTO dto);
}