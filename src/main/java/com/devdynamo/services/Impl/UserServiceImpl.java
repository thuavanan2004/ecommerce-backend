package com.devdynamo.services.Impl;

import com.devdynamo.dtos.request.RegisterRequest;
import com.devdynamo.dtos.request.UserRequestDTO;
import com.devdynamo.dtos.response.UserResponseDTO;
import com.devdynamo.entities.UserEntity;
import com.devdynamo.exceptions.ResourceNotFoundException;
import com.devdynamo.mappers.UserMapper;
import com.devdynamo.repositories.UserRepository;
import com.devdynamo.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private UserEntity getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public UserResponseDTO getUser(Long id) {
        UserEntity record = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDTO(record);
    }

    @Override
    public long saveUser(UserEntity userEntity) {
        userRepository.save(userEntity);
        return userEntity.getId();
    }

    @Override
    public void updateUser(long userId, UserRequestDTO request) {
        UserEntity user = getUserById(userId);
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());

        userRepository.save(user);
        log.info("User updated successfully");
    }


}
