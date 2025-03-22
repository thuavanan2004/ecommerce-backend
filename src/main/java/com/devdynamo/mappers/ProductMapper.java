package com.devdynamo.mappers;

import com.devdynamo.dtos.request.ProductRequestDTO;
import com.devdynamo.dtos.response.ProductResponseDTO;
import com.devdynamo.entities.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mappings({@Mapping(source = "category.id", target = "categoryId"),
            @Mapping(source = "category.name", target = "categoryName")})
    ProductResponseDTO toDTO(ProductEntity product);

    @Mapping(target = "category", ignore = true)
    ProductEntity toEntity(ProductRequestDTO dto);

    @Mapping(target = "category", ignore = true)
    void updateProductFromDTO(ProductRequestDTO dto, @MappingTarget ProductEntity entity);
}
