package com.devdynamo.service;

import com.devdynamo.entities.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public class CartItemService extends baseServiceImpl<CartItemEntity, Long>{
    public CartItemService(JpaRepository<CartItemEntity, Long> jpaRepository) {
        super(jpaRepository);
    }
}
