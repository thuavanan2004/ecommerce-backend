package com.devdynamo.mappers;

import com.devdynamo.dtos.request.ProductRequestDTO;
import com.devdynamo.dtos.response.ProductResponseDTO;
import com.devdynamo.entities.ProductEntity;
import com.devdynamo.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mappings({@Mapping(source = "category.id", target = "categoryId"),
            @Mapping(source = "category.name", target = "categoryName")})
    ProductResponseDTO toDTO(ProductEntity product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    ProductEntity toEntity(ProductRequestDTO dto);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    void updateProductFromDTO(ProductRequestDTO dto, @MappingTarget ProductEntity entity);

    @Named("mapMultipartFileToString")
    default String mapMultipartFileToString(MultipartFile file){
        return (file != null && !file.isEmpty() ? file.getOriginalFilename() : null);
    }
}
