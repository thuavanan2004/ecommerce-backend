package com.devdynamo.services;

import com.devdynamo.dtos.request.ProductRequestDTO;
import com.devdynamo.dtos.response.PageResponse;
import com.devdynamo.dtos.response.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO getProductById(long id);

    PageResponse<?> getAllProducts(int pageNo, int pageSize, String search, String sortBy);

    void updateProduct(long productId, ProductRequestDTO request);

    void createProduct(ProductRequestDTO request);

    void deleteProduct(long productId);
}
