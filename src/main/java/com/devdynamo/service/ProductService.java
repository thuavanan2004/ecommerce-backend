package com.devdynamo.service;

import com.devdynamo.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public class ProductService extends baseServiceImpl<ProductEntity, Long>{
    public ProductService(JpaRepository<ProductEntity, Long> jpaRepository) {
        super(jpaRepository);
    }
}
