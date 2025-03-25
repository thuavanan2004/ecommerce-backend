package com.devdynamo.controllers.client;


import com.devdynamo.dtos.response.ResponseData;
import com.devdynamo.dtos.response.ResponseError;
import com.devdynamo.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client/categories")
@Validated
@Tag(name="Client category")
@Slf4j
@RequiredArgsConstructor
public class ClientCategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Get list category")
    @GetMapping("/list")
    public ResponseData<?> getListCategory(){
        log.info("Get list category");
        try{
            return new ResponseData<>(HttpStatus.OK.value(), "Get list category successfully", categoryService.getAllCategoryForClient());
        }catch (Exception e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get list category failed");
        }
    }
}
