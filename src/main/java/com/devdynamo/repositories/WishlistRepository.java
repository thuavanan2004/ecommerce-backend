package com.devdynamo.repositories;

import com.devdynamo.entities.WishListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishListEntity, Long> {
    Optional<WishListEntity> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    List<WishListEntity> findByUserId(Long userId);
}
