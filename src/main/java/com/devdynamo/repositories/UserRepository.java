package com.devdynamo.repositories;

import com.devdynamo.entities.UserEntity;
import com.devdynamo.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Long countByRole(Role role);
}
