package com.devdynamo.service;

import com.devdynamo.entities.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public class ReviewService extends baseServiceImpl<ReviewEntity, Long>{
    public ReviewService(JpaRepository<ReviewEntity, Long> jpaRepository) {
        super(jpaRepository);
    }
}
