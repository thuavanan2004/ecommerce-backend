package com.devdynamo.services.Impl;

import com.devdynamo.dtos.request.UserRequestDTO;
import com.devdynamo.dtos.response.PageResponse;
import com.devdynamo.dtos.response.UserResponseDTO;
import com.devdynamo.entities.UserEntity;
import com.devdynamo.enums.Role;
import com.devdynamo.enums.UserStatus;
import com.devdynamo.exceptions.InvalidDataException;
import com.devdynamo.exceptions.ResourceNotFoundException;
import com.devdynamo.mappers.UserMapper;
import com.devdynamo.repositories.UserRepository;
import com.devdynamo.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.devdynamo.utils.AppConst.SORT_BY;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService  {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private UserEntity getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public UserResponseDTO getUser(long id) {
        UserEntity record = getUserById(id);
        return userMapper.toDTO(record);
    }

    @Override
    public void saveUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    @Override
    public void createUser(UserRequestDTO request) {
        userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new InvalidDataException("Email already exists"));
        UserEntity user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        log.info("Create user successfully");
    }

    @Override
    public void updateUser(long userId, UserRequestDTO request) {
        UserEntity user = getUserById(userId);
        if(user.getEmail().equals(request.getEmail())){
           throw new ResourceNotFoundException("Email must be not change");
        }
        userMapper.updateUserFromDTO(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        log.info("User updated successfully");
    }

    @Override
    public void changeStatus(long userId, UserStatus status) {
        UserEntity user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);

        log.info("status changed");
    }

    @Override
    public void changeRole(long userId, Role role) {
        UserEntity user = getUserById(userId);
        user.setRole(role);
        userRepository.save(user);

        log.info("Role changed");
    }

    @Override
    public String confirmUser(long userId, String verifyCode) {
        return "Confirmed!";
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.findById(userId);
        userRepository.deleteById(userId);

        log.info("Delete user successfully");
    }

    @Override
    public PageResponse<?> getAllUsersWithSortBy(int pageNo, int pageSize, String... sorts) {
        int page = 0;
        if(pageNo > 0){
            page = pageNo - 1;
        }

        List<Sort.Order> orders = new ArrayList<>();

        for (String sortBy : sorts){
            if(StringUtils.hasLength(sortBy)){
                Pattern pattern = Pattern.compile(SORT_BY);
                Matcher matcher = pattern.matcher(sortBy);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }


        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(orders));
        Page<UserEntity> users = userRepository.findAll(pageable);
        List<UserResponseDTO> list = users.stream().map(userMapper::toDTO).toList();
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(users.getTotalPages())
                .items(list)
                .build();
    }


}
