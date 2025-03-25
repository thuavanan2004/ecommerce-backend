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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.devdynamo.utils.AppConst.SORT_BY;

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
    public PageResponse<?> getAllCategoryForAdmin(int pageNo, int pageSize, String sort) {
        int page = 0;
        if(pageNo > 0){
            page = pageNo - 1;
        }

        List<Sort.Order> orders = new ArrayList<>();
        if(StringUtils.hasLength(sort)){
            Pattern pattern = Pattern.compile(SORT_BY);
            Matcher matcher = pattern.matcher(sort);

            if(matcher.find()){
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(orders));
        Page<CategoryEntity> categories = categoryRepository.findAll(pageable);
        List<CategoryResponseDTO> list = categories.stream().map(categoryMapper::toDTO).toList();
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(categories.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public List<CategoryResponseDTO> getAllCategoryForClient() {
        log.info("Service: Get list category for client");

        return categoryRepository.findAll().stream().map(categoryMapper::toDTO).toList();
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
