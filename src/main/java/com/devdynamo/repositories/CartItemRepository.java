package com.devdynamo.repositories;

import com.devdynamo.entities.CartItemEntity;
import com.devdynamo.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findAllByCartId(long cartId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CartItemEntity c WHERE c.cart.id = :cartId")
    void deleteByCartId(Long cartId);


    Optional<CartItemEntity> findByCartIdAndProductId(long cartId, long productId);
}
