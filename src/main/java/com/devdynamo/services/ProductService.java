package com.devdynamo.services;

import com.devdynamo.dtos.request.ProductRequestDTO;
import com.devdynamo.dtos.response.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO getProductById(Long id);
    List<ProductResponseDTO> getAll();
}
