package com.devdynamo.services.Impl;

import com.devdynamo.dtos.request.ProductRequestDTO;
import com.devdynamo.dtos.response.ProductResponseDTO;
import com.devdynamo.entities.ProductEntity;
import com.devdynamo.exceptions.ResourceNotFoundException;
import com.devdynamo.mappers.ProductMapper;
import com.devdynamo.repositories.ProductRepository;
import com.devdynamo.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    ProductMapper productMapper;


    @Override
    public ProductResponseDTO getProductById(Long id) {
        ProductEntity record = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return productMapper.toDTO(record);
    }

    @Override
    public List<ProductResponseDTO> getAll() {
        List<ProductEntity> list = productRepository.findAll();
        return productRepository.findAll().stream().map(productMapper :: toDTO).collect(Collectors.toList());
    }
}
