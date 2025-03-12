package com.devdynamo.services;

import com.devdynamo.dtos.ProductDTO;

import java.util.List;

public interface ProductService {
    ProductDTO getProductById(Long id);
    List<ProductDTO> getAll();
}
