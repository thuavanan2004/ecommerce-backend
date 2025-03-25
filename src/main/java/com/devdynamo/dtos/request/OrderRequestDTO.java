package com.devdynamo.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {
    @NotNull(message = "userId must be not null")
    private Long userId;

    @NotNull(message = "items must be not null")
    private List<OrderItemRequestDTO> items;
}
