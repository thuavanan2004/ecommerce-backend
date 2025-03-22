package com.devdynamo.dtos.response;

import com.devdynamo.enums.Role;
import com.devdynamo.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO{
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private UserStatus status;
    private String address;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
