package com.devdynamo.services.Impl;

import com.devdynamo.dtos.request.RegisterRequest;
import com.devdynamo.dtos.response.UserResponseDTO;
import com.devdynamo.entities.UserEntity;
import com.devdynamo.exceptions.ResourceNotFoundException;
import com.devdynamo.mappers.UserMapper;
import com.devdynamo.repositories.UserRepository;
import com.devdynamo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserResponseDTO getUser(Long id) {
        UserEntity record = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDTO(record);
    }

}
