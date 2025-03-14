package com.devdynamo.services;

import com.devdynamo.dtos.request.UserRequestDTO;
import com.devdynamo.dtos.response.UserResponseDTO;

public interface UserService {
    UserResponseDTO getUser(Long id);
}
