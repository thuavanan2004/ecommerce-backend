package com.devdynamo.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemRequestDTO {
    @NotNull(message = "productId must be not null")
    private Long productId;

    @NotNull(message = "quantity must be not null")
    private Integer quantity;

    @NotNull(message = "price must be not null")
    private BigDecimal price;
}
