package com.devdynamo.services;

import com.devdynamo.dtos.request.CategoryRequestDTO;
import com.devdynamo.dtos.response.CategoryResponseDTO;
import com.devdynamo.dtos.response.PageResponse;

public interface CategoryService {
    CategoryResponseDTO getCategory(long categoryId);

    PageResponse<?> getAllCategory(int pageNo, int pageSize, String sort);

    void createCategory(CategoryRequestDTO request);

    void updateCategory(long categoryId, CategoryRequestDTO request);

    void deleteCategory(long categoryId);
}
