package com.devdynamo.controllers.client;

import com.devdynamo.dtos.request.MergeCartRequest;
import com.devdynamo.dtos.response.ResponseData;
import com.devdynamo.dtos.response.ResponseError;
import com.devdynamo.entities.RedisCart;
import com.devdynamo.services.RedisCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/carts")
@Slf4j
@Tag(name="Client carts")
@RequiredArgsConstructor
public class ClientCartController {
    private final RedisCartService redisCartService;

    @Operation(summary = "Add product for cart save on redis cache")
    @PostMapping("/add")
    public ResponseData<?> addToCartOnRedis(@RequestParam String sessionId, @RequestBody RedisCart cart) {
        log.info("Add product for cart save on redis cache");
        try{
            redisCartService.addItemToCart(sessionId, cart);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Add product for cart on redis successfully");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Add cart for user failed");
        }
    }

    @Operation(summary = "Get list product for cart save on redis cache")
    @GetMapping("/get")
    public ResponseData<?> getCartOnRedis(@RequestParam String sessionId) {
        log.info("Get product for cart save on redis cache");
        try{
            return new ResponseData<>(HttpStatus.CREATED.value(), "Get list product for cart on redis successfully", redisCartService.getCart(sessionId));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get list product for cart on redis failed");
        }
    }

    @Operation(summary = "Remove product from cart")
    @DeleteMapping("/remove")
    public ResponseData<?> removeItemFromCart(@RequestParam String sessionId, @RequestParam long productId) {
        log.info("Remove product from cart");
        try{
            redisCartService.removeItemFromCart(sessionId, productId);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Remove product from cart successfully");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Remove product from cart failed");
        }
    }

    @Operation(summary = "Merge cart for user")
    @PostMapping("/merge")
    public ResponseData<?> mergeCart(@Valid @RequestBody MergeCartRequest request){
        log.info("Merge cart for user");
        try{
            redisCartService.mergeCart(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Merge cart for user successfully");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Merge cart for user failed");
        }
    }


}
