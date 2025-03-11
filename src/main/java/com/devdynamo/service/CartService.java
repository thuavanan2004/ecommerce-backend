package com.devdynamo.service;

import com.devdynamo.entities.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService extends baseServiceImpl<CartEntity, Long>{
    public CartService(JpaRepository<CartEntity, Long> jpaRepository) {
        super(jpaRepository);
    }
}
