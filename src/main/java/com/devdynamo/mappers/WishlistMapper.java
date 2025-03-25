package com.devdynamo.mappers;

import com.devdynamo.dtos.response.WishlistReponseDTO;
import com.devdynamo.entities.WishListEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface WishlistMapper {
    @Mappings({@Mapping(source = "product.id", target = "productId"),
            @Mapping(source = "product.name", target = "productName"),
            @Mapping(source = "product.imageUrl", target = "productImageUrl"),
            @Mapping(source = "product.price", target = "productPrice"),
            @Mapping(source = "product.discount", target = "productDiscount"),
            @Mapping(source = "user.id", target = "userId")})
    WishlistReponseDTO toDTO(WishListEntity entity);
}
