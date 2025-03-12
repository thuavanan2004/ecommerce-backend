package com.devdynamo.services;

import com.devdynamo.dtos.ProductDTO;

public interface ProductService {
    ProductDTO getProductById(Long id);
}
