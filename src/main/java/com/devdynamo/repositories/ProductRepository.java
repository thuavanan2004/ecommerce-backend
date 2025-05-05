package com.devdynamo.repositories;

import com.devdynamo.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    Long countByDeletedFalse();

    ProductEntity getBySlug(String slug);
}
