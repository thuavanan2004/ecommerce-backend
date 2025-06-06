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
import com.devdynamo.repositories.specification.ProductSpecificationBuilder;
import com.devdynamo.services.CloudinaryService;
import com.devdynamo.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.devdynamo.utils.AppConst.SEARCH_SPEC_OPERATOR;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final SearchRepository searchRepository;

    private final ProductMapper productMapper;

    private final CloudinaryService cloudinaryService;

    private ProductEntity getProductEntityById(long productId){
        return productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public ProductResponseDTO getProductById(long productId) {
        ProductEntity record = getProductEntityById(productId);
        return productMapper.toDTO(record);
    }

    @Override
    public ProductResponseDTO getProduct(String slug) {
        ProductEntity record = productRepository.getBySlug(slug);
        return productMapper.toDTO(record);
    }


    @Override
    public PageResponse<?> getAllProductForAdmin(int pageNo, int pageSize, String search, String sortBy) {
        return searchRepository.getProducts(pageNo, pageSize, search, sortBy);
    }

    @Override
    public PageResponse<?> getAllProductForClient(Pageable pageable, String[] products, String[] categories) {
        boolean hasProductFilters = products != null && products.length > 0;
        boolean hasCategoryFilters = categories != null && categories.length > 0;

        if (hasProductFilters && hasCategoryFilters) {
            // Có cả 2 điều kiện -> dùng join query
            return searchRepository.searchProductByCriteriaWithJoin(pageable, products, categories);

        } else if (hasProductFilters) {
            // Chỉ lọc theo sản phẩm
            ProductSpecificationBuilder builder = new ProductSpecificationBuilder();
            Pattern pattern = Pattern.compile(SEARCH_SPEC_OPERATOR);
            for (String p : products){
                Matcher matcher = pattern.matcher(p);
                if(matcher.find()){
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }
            Page<ProductEntity> records = productRepository.findAll(Objects.requireNonNull(builder.build()), pageable);

            List<ProductResponseDTO> results = records.stream().map(productMapper::toDTO).toList();
            return PageResponse.builder()
                    .pageNo(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .totalPage(records.getTotalPages())
                    .items(results)
                    .build();

        } else if (hasCategoryFilters) {
            // Chỉ lọc theo category -> vẫn dùng join để hỗ trợ lọc theo category
            return searchRepository.searchProductByCriteriaWithJoin(pageable, new String[]{}, categories);

        } else {
            // Không có điều kiện nào -> trả về tất cả
            Page<ProductEntity> records = productRepository.findAll(pageable);
            List<ProductResponseDTO> results = records.stream().map(productMapper::toDTO).toList();

            return PageResponse.builder()
                    .pageNo(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .totalPage(records.getTotalPages())
                    .items(results)
                    .build();
        }

    }


    @Override
    public void updateProduct(long productId, ProductRequestDTO request) {
        ProductEntity product = getProductEntityById(productId);
        CategoryEntity category = categoryRepository.findById(request.getCategoryId()).orElseThrow(()-> new ResourceNotFoundException("Category not found"));
        productMapper.updateProductFromDTO(request, product);
        product.setCategory(category);
        if(!product.getImageUrl().isEmpty()){
            product.setImageUrl(cloudinaryService.uploadSingleFile(request.getImageUrl()));
        }
        productRepository.save(product);
        log.info("Update product successfully");
    }

    @Override
    public void createProduct(ProductRequestDTO request) {
        CategoryEntity category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));;
        ProductEntity product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setImageUrl(cloudinaryService.uploadSingleFile(request.getImageUrl()));
        product.setSlug(product.getName().toLowerCase().replace(" ", "-"));

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
