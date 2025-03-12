package com.devdynamo.mappers;

import com.devdynamo.dtos.ProductDTO;
import com.devdynamo.entities.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);


    ProductDTO toDTO(ProductEntity product);

    ProductEntity toEntity(ProductDTO dto);
}