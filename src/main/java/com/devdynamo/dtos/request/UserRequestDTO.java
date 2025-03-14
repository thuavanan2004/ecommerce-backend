package com.devdynamo.dtos.request;

import com.devdynamo.enums.Role;
import com.devdynamo.validators.EnumPattern;
import com.devdynamo.validators.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
public class UserRequestDTO{
    @NotBlank(message = "FullName must be not blank")
    private String fullName;

    @NotBlank(message = "Email must be not blank")
    @Email(message = "Email format invalid")
    private String email;

    @NotNull(message = "Password must be not null")
    private String password;

    @PhoneNumber
    private String phone;

    @NotEmpty(message = "Address must be not empty")
    private String address;

    @EnumPattern( name="role", regexp = "customer|admin")
    private Role role;
}
