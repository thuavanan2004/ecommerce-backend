package com.devdynamo.controllers.admin;

import com.devdynamo.dtos.request.ProductRequestDTO;
import com.devdynamo.dtos.response.ProductResponseDTO;
import com.devdynamo.dtos.response.ResponseData;
import com.devdynamo.dtos.response.ResponseSuccess;
import com.devdynamo.services.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/auth/products")
@Tag(name= "Products")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<ProductResponseDTO> getById(@Min(1) @PathVariable Long id){
        return new ResponseData<>(HttpStatus.OK.value(), "Get product success", productService.getProductById(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ProductResponseDTO>> getAll(){
        return new ResponseData<>(HttpStatus.OK.value(), "Get list product success", productService.getAll());
    }
}
