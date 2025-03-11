package com.devdynamo.service;

import com.devdynamo.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public class OrderService extends baseServiceImpl<OrderEntity, Long>{
    public OrderService(JpaRepository<OrderEntity, Long> jpaRepository) {
        super(jpaRepository);
    }
}
