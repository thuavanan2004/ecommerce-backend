package com.devdynamo.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProductResponseDTO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discount;
    private Integer stockQuantity;
    private TreeSet<Double> size;
    private Long categoryId;
    private String categoryName;
    private Set<String> imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
