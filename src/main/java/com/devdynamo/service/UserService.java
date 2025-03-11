package com.devdynamo.service;

import com.devdynamo.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public class UserService extends baseServiceImpl<UserEntity, Long>{
    public UserService(JpaRepository<UserEntity, Long> jpaRepository) {
        super(jpaRepository);
    }
}
