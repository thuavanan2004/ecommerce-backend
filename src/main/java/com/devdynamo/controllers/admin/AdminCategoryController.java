package com.devdynamo.controllers.admin;

import com.devdynamo.dtos.request.CategoryRequestDTO;
import com.devdynamo.dtos.response.ResponseData;
import com.devdynamo.dtos.response.ResponseError;
import com.devdynamo.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
@Tag(name = "Admin category")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Get detail info category")
    @GetMapping("/{categoryId}")
    public ResponseData<?> getCategory(@Min(1) @PathVariable long categoryId){
        log.info("Get info category by categoryId={}", categoryId);
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Get info category successfully", categoryService.getCategory(categoryId));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get info category failed");
        }
    }

    @Operation(summary = "Get list category")
    @GetMapping("/list")
    public ResponseData<?> getAllCategory(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                          @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
                                          @RequestParam(required = false) String sort){
        log.info("Get list category");
        try{
            return new ResponseData<>(HttpStatus.OK.value(), "Get list category successfully", categoryService.getAllCategoryForAdmin(pageNo, pageSize, sort));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get list category failed");
        }
    }

    @Operation(summary = "Create category")
    @PostMapping("")
    public ResponseData<?> createCategory(@Valid @RequestBody CategoryRequestDTO request){
        log.info("Create category");
        try{
            categoryService.createCategory(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Create category successfully");
        }catch (Exception e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Create category failed");
        }
    }

    @Operation(summary = "Update category")
    @PutMapping("/{categoryId}")
    public ResponseData<?> updateCategory(@PathVariable long categoryId, @Valid @RequestBody CategoryRequestDTO request){
        log.info("Update category");
        try{
            categoryService.updateCategory(categoryId, request);
            return new ResponseData<>(HttpStatus.OK.value(), "Update category successfully");
        }catch (Exception e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update category failed");
        }
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/{categoryId}")
    public ResponseData<?> deleteCategory(@PathVariable long categoryId){
        log.info("Delete category");
        try{
            categoryService.deleteCategory(categoryId);
            return new ResponseData<>(HttpStatus.OK.value(), "Delete category successfully");
        }catch (Exception e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete category failed");
        }
    }
}
