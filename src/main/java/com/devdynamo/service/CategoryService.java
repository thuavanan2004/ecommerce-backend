package com.devdynamo.service;

import com.devdynamo.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public class CategoryService extends baseServiceImpl<CategoryEntity, Long>{
    public CategoryService(JpaRepository<CategoryEntity, Long> jpaRepository) {
        super(jpaRepository);
    }
}
