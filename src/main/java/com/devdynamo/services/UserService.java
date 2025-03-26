package com.devdynamo.services;

import com.devdynamo.dtos.request.UserRequestDTO;
import com.devdynamo.dtos.response.PageResponse;
import com.devdynamo.dtos.response.UserResponseDTO;
import com.devdynamo.entities.UserEntity;
import com.devdynamo.enums.Role;
import com.devdynamo.enums.UserStatus;

public interface UserService {
//    UserDetailsService userDetailsService();

    UserResponseDTO getUser(long id);

    UserEntity getUserByEmail(String email);

    void saveUser(UserEntity userEntity);

    void createUser(UserRequestDTO request);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void changeRole(long userId, Role role);

    void deleteUser(long userId);

    PageResponse<?> getAllUsersWithSortBy(int pageNo, int pageSize, String... sorts);
}
