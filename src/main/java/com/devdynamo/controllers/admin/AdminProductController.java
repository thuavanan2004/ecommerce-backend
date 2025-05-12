package com.devdynamo.controllers.admin;

import com.devdynamo.dtos.request.ProductRequestDTO;
import com.devdynamo.dtos.response.ResponseData;
import com.devdynamo.dtos.response.ResponseError;
import com.devdynamo.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/products")
@Tag(name= "Admin products")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @Operation(summary = "Get product", description = "Lấy thông tin sản phẩm!")
    @GetMapping("/{productId}")
    public ResponseData<?> getProduct(@Min(1) @PathVariable long productId){
        log.info("Get product by productId={}", productId);
        try{
            return new ResponseData<>(HttpStatus.OK.value(), "Get product success", productService.getProductById(productId));
        } catch (Exception e){
            log.info("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get product failed");
        }

    }

    @Operation(summary = "Get list product", description = "Lấy danh sách sản phẩm!")
    @GetMapping("/list")
    public ResponseData<?> getAll(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                 @RequestParam(defaultValue = "20", required = false) int pageSize,
                                 @RequestParam(required = false) String search,
                                 @RequestParam(required = false) String sortBy){
        log.info("Get list product");
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Get list product success", productService.getAllProductForAdmin(pageNo, pageSize, search, sortBy));
        } catch (Exception e){
            log.info("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get list product failed");
        }

    }

    @Operation(summary = "Update product", description = "Cập nhật sản phẩm")
    @PutMapping("/{productId}")
    public ResponseData<?> updateProduct(@PathVariable @Min(1) long productId, @Valid @ModelAttribute ProductRequestDTO request){
        log.info("Update product by productId={}", productId);
        try{
            productService.updateProduct(productId, request);
            return new ResponseData<>(HttpStatus.OK.value(), "Update product success");
        } catch (Exception e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update product failed");
        }
    }

    @Operation(summary = "Create product", description = "Tạo mới sản phẩm")
    @PostMapping()
    public ResponseData<?> createProduct(@Valid @ModelAttribute ProductRequestDTO request){
        log.info("Create product");
        try{
            productService.createProduct(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Create product success");
        } catch (Exception e) {
            log.info("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Create product failed");
        }
    }

    @Operation(summary = "Delete product", description = "Xóa vĩnh viễn sản phẩm")
    @DeleteMapping("/{productId}")
    public ResponseData<?> deleteProduct(@PathVariable long productId){
        log.info("Delete product by productId={}", productId);
        try{
            productService.deleteProduct(productId);
            return new ResponseData<>(HttpStatus.OK.value(), "Delete product success");
        }catch (Exception e){
            log.info("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete product failed");
        }
    }
}
