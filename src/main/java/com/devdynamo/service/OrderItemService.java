package com.devdynamo.service;

import com.devdynamo.entities.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public class OrderItemService extends baseServiceImpl<OrderItemEntity, Long>{
    public OrderItemService(JpaRepository<OrderItemEntity, Long> jpaRepository) {
        super(jpaRepository);
    }
}
