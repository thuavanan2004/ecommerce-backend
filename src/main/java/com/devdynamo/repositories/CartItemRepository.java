package com.devdynamo.repositories;

import com.devdynamo.entities.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findAllByCartId(long cartId);
}
