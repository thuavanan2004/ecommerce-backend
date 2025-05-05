package com.devdynamo.services;

import com.devdynamo.dtos.request.ProductRequestDTO;
import com.devdynamo.dtos.response.PageResponse;
import com.devdynamo.dtos.response.ProductResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


public interface ProductService {
    ProductResponseDTO getProductById(long productId);

    ProductResponseDTO getProduct(String slug);

    PageResponse<?> getAllProductForAdmin(int pageNo, int pageSize, String search, String sortBy);

    PageResponse<?> getAllProductForClient(Pageable pageable, String[] products, String[] categories);

    void updateProduct(long productId, ProductRequestDTO request);

    void createProduct(ProductRequestDTO request);

    void deleteProduct(long productId);
}
