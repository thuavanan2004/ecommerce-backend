package com.devdynamo.service;

import com.devdynamo.entities.WishListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public class WishListService extends baseServiceImpl<WishListEntity, Long >{
    public WishListService(JpaRepository<WishListEntity, Long> jpaRepository) {
        super(jpaRepository);
    }
}
