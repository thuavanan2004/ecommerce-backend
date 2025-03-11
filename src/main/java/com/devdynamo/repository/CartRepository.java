package com.devdynamo.repository;

import com.devdynamo.entities.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository  extends JpaRepository<CartEntity, Long> {
}
