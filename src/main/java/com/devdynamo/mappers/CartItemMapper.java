package com.devdynamo.mappers;

import com.devdynamo.entities.CartItemEntity;
import com.devdynamo.entities.RedisCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mappings({
            @Mapping(source = "product.id", target = "productId"),
            @Mapping(source = "product.name", target = "name"),
            @Mapping(source = "product.price", target = "price"),
            @Mapping(source = "product.imageUrl", target = "image")
    })
    RedisCart toRedisCart(CartItemEntity entity);
}
