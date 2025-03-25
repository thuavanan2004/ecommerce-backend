package com.devdynamo.mappers;

import com.devdynamo.dtos.response.OrderItemResponseDTO;
import com.devdynamo.entities.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mappings({
            @Mapping(source = "product.id", target = "productId"),
            @Mapping(source = "product.name", target = "productName"),
            @Mapping(source = "product.imageUrl", target = "imageUrl"),
            @Mapping(source = "price", target = "price"),
            @Mapping(source = "quantity", target = "quantity")
    })
    OrderItemResponseDTO toDTO(OrderItemEntity entity);
}
