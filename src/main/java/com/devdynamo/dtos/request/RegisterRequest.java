package com.devdynamo.dtos.request;


import com.devdynamo.validators.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "fullName must be not blank")
    private String fullName;

    @NotBlank(message = "email must be not blank")
    @Email
    private String email;

    @NotBlank(message = "password must be not blank")
    private String password;

    @NotNull
    @PhoneNumber
    private String phone;
    private String address;
}
