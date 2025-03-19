package com.devdynamo.services;

import com.devdynamo.dtos.request.RegisterRequest;
import com.devdynamo.dtos.request.UserRequestDTO;
import com.devdynamo.dtos.response.UserResponseDTO;
import com.devdynamo.entities.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService {
    UserDetailsService userDetailsService();

    UserResponseDTO getUser(Long id);

    UserEntity getUserByEmail(String email);

    long saveUser(UserEntity userEntity);

    void updateUser(long userId, UserRequestDTO request);

}
