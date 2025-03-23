package com.devdynamo.services.Impl;

import com.devdynamo.dtos.request.CategoryRequestDTO;
import com.devdynamo.dtos.response.CategoryResponseDTO;
import com.devdynamo.dtos.response.PageResponse;
import com.devdynamo.entities.CategoryEntity;
import com.devdynamo.exceptions.ResourceNotFoundException;
import com.devdynamo.mappers.CategoryMapper;
import com.devdynamo.repositories.CategoryRepository;
import com.devdynamo.services.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponseDTO getCategory(long categoryId) {
        log.info("Service: Get info category by categoryId={}", categoryId);
        CategoryEntity category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
        return categoryMapper.toDTO(category);
    }

    @Override
    public PageResponse<?> getAllCategory(int pageNo, int pageSize, String search, String sort) {
        return null;
    }

    @Override
    public void createCategory(CategoryRequestDTO request) {
        CategoryEntity category = categoryMapper.toEntity(request);
        categoryRepository.save(category);

        log.info("Service: Create category successfully");
    }

    @Override
    public void updateCategory(long categoryId, CategoryRequestDTO request) {
        CategoryEntity category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setName(request.getName());

        categoryRepository.save(category);
        log.info("Service: Update category successfully");
    }

    @Override
    public void deleteCategory(long categoryId) {
        categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        categoryRepository.deleteById(categoryId);

        log.info("Service: Delete category successfully");
    }
}
