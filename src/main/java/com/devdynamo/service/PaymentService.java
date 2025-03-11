package com.devdynamo.service;

import com.devdynamo.entities.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public class PaymentService extends baseServiceImpl<PaymentEntity, Long>{
    public PaymentService(JpaRepository<PaymentEntity, Long> jpaRepository) {
        super(jpaRepository);
    }
}
