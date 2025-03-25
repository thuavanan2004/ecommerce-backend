package com.devdynamo.services.Impl;

import com.devdynamo.dtos.request.ProductRequestDTO;
import com.devdynamo.dtos.response.PageResponse;
import com.devdynamo.dtos.response.ProductResponseDTO;
import com.devdynamo.entities.CategoryEntity;
import com.devdynamo.entities.ProductEntity;
import com.devdynamo.exceptions.ResourceNotFoundException;
import com.devdynamo.mappers.ProductMapper;
import com.devdynamo.repositories.CategoryRepository;
import com.devdynamo.repositories.ProductRepository;
import com.devdynamo.repositories.SearchRepository;
import com.devdynamo.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final SearchRepository searchRepository;

    private final ProductMapper productMapper;

    private ProductEntity getProductEntityById(long productId){
        return productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public ProductResponseDTO getProductById(long productId) {
        ProductEntity record = getProductEntityById(productId);
        return productMapper.toDTO(record);
    }

    @Override
    public PageResponse<?> getAllProductForAdmin(int pageNo, int pageSize, String search, String sortBy) {
        return searchRepository.getProducts(pageNo, pageSize, search, sortBy);
    }

    @Override
    public PageResponse<?> getAllProductForClient(Pageable pageable, String[] products, String[] categories) {
        return null;
    }


    @Override
    public void updateProduct(long productId, ProductRequestDTO request) {
        ProductEntity product = getProductEntityById(productId);
        CategoryEntity category = categoryRepository.findById(request.getCategoryId()).orElseThrow(()-> new ResourceNotFoundException("Category not found"));
        productMapper.updateProductFromDTO(request, product);
        product.setCategory(category);

        productRepository.save(product);
        log.info("Update product successfully");
    }

    @Override
    public void createProduct(ProductRequestDTO request) {
        CategoryEntity category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        ProductEntity product = productMapper.toEntity(request);
        product.setCategory(category);
        productRepository.save(product);

        log.info("Create product successfully");
    }

    @Override
    public void deleteProduct(long productId) {
        getProductEntityById(productId);
        productRepository.deleteById(productId);

        log.info("Delete product successfully");
    }

}
