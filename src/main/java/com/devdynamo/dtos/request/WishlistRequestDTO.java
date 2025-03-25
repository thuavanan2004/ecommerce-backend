package com.devdynamo.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WishlistRequestDTO {
    @NotNull(message = "userId must be not null")
    private long userId;

    @NotNull(message = "productId must be not null")
    private long productId;
}
