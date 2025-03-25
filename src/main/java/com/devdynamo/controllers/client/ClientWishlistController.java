package com.devdynamo.controllers.client;

import com.devdynamo.dtos.request.WishlistRequestDTO;
import com.devdynamo.dtos.response.ResponseData;
import com.devdynamo.dtos.response.ResponseError;
import com.devdynamo.services.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/wishlist")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Client wishlist")
public class ClientWishlistController {
    private final WishlistService wishlistService;

    @Operation(summary = "Add product to wishlist")
    @PostMapping("")
    public ResponseData<?> addProductToWishlist(@Valid @RequestBody WishlistRequestDTO request){
        log.info("Client: Add product to wishlist");
        try{
            wishlistService.addProductToWishlist(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Add product to wishlist successfully");
        }catch (Exception e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Add product to wishlist failed");
        }
    }

    @Operation(summary = "Remove product from wishlist")
    @DeleteMapping("")
    public ResponseData<?> deleteProductFromWishlist(@Valid @RequestBody WishlistRequestDTO request){
        log.info("Client: Remove product from wishlist");
        try{
            wishlistService.removeProductFromWishlist(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Remove product from wishlist successfully");
        }catch (Exception e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Remove product from wishlist failed");
        }
    }

    @Operation(summary = "Get all product from wishlist")
    @GetMapping("/list/{userId}")
    public ResponseData<?> getAllProductFromWishlist(@PathVariable @Min(1) long userId){
        log.info("Client: Get all product from wishlist");
        try{
            return new ResponseData<>(HttpStatus.OK.value(), "Get all product from wishlist successfully", wishlistService.getAllProductFromWishlist(userId));
        }catch (Exception e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get all product from wishlist failed");
        }
    }
}
