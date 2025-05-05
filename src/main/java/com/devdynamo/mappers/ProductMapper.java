package com.devdynamo.mappers;

import com.devdynamo.dtos.request.ProductRequestDTO;
import com.devdynamo.dtos.response.ProductResponseDTO;
import com.devdynamo.entities.ProductEntity;
import com.devdynamo.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mappings({@Mapping(source = "category.id", target = "categoryId"),
            @Mapping(source = "category.name", target = "categoryName"),
            @Mapping(source = "slug", target = "slug"),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "imageUrl", ignore = true)})
    ProductResponseDTO toDTO(ProductEntity product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    ProductEntity toEntity(ProductRequestDTO dto);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    void updateProductFromDTO(ProductRequestDTO dto, @MappingTarget ProductEntity entity);

    @AfterMapping
    default void mapCustomFields(ProductEntity product, @MappingTarget ProductResponseDTO.ProductResponseDTOBuilder dto) {
        TreeSet<Double> sizes = product.getSize().isBlank() ? new TreeSet<>() :Arrays.stream(product.getSize().split(","))
                .map(Double::parseDouble)
                .collect(Collectors.toCollection(TreeSet::new));

        Set<String> images = product.getImageUrl().isBlank() ? new HashSet<>() : Arrays.stream(product.getImageUrl().split(","))
                .collect(Collectors.toSet());

        dto.size(sizes);
        dto.imageUrl(images);
    }
}
