package com.devdynamo.controllers.client;

import com.devdynamo.dtos.response.ResponseData;
import com.devdynamo.dtos.response.ResponseError;
import com.devdynamo.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/products")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Client product")
public class ClientProductController {
    private final ProductService productService;

    @Operation(summary = "Get product")
    @GetMapping("/{slug}")
    public ResponseData<?> getProduct(@PathVariable String slug){
        log.info("Client: Get product with slug={}", slug);
        try{
            return new ResponseData<>(HttpStatus.OK.value(), "Get product successfully", productService.getProduct(slug));
        }catch (Exception e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get product failed");
        }
    }


    @Operation(summary = "Get product")
    @GetMapping("/list")
    public ResponseData<?> getAllProduct(Pageable pageable,
                                         @RequestParam(required = false) String[] products,
                                         @RequestParam(required = false) String[] categories){
        log.info("Client: Get list product");
        try{
            return new ResponseData<>(HttpStatus.OK.value(), "Get list product successfully", productService.getAllProductForClient(pageable, products, categories));
        }catch (Exception e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get list product failed");
        }
    }
}
