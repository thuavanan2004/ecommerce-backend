package com.devdynamo.services;

import com.devdynamo.dtos.request.CategoryRequestDTO;
import com.devdynamo.dtos.response.CategoryResponseDTO;
import com.devdynamo.dtos.response.PageResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponseDTO getCategory(long categoryId);

    PageResponse<?> getAllCategoryForAdmin(int pageNo, int pageSize, String sort);

    List<CategoryResponseDTO> getAllCategoryForClient();

    void createCategory(CategoryRequestDTO request);

    void updateCategory(long categoryId, CategoryRequestDTO request);

    void deleteCategory(long categoryId);
}
