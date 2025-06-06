package com.devdynamo.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MergeCartRequest {
    @NotNull(message = "userId must be not null")
    private Long userId;

    @NotBlank(message = "sessionId mus be not blank")
    private String sessionId;
}
