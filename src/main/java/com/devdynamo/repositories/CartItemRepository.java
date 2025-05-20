package com.devdynamo.repositories;

import com.devdynamo.entities.CartItemEntity;
import com.devdynamo.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findAllByCartId(long cartId);


    Optional<CartItemEntity> findByCartIdAndProductId(long cartId, long productId);
}
