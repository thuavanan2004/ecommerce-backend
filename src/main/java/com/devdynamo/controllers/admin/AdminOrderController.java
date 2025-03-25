package com.devdynamo.controllers.admin;

import com.devdynamo.dtos.response.ResponseData;
import com.devdynamo.dtos.response.ResponseError;
import com.devdynamo.enums.OrderStatus;
import com.devdynamo.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
@Validated
@Tag(name = "Admin orders")
public class AdminOrderController {
    private final OrderService orderService;

    @Operation(summary = "Get detail order")
    @GetMapping("/{orderId}")
    public ResponseData<?> getOrder(@Min(1) @PathVariable long orderId){
        log.info("Get detail order with orderId={}", orderId);
        try{
            return new ResponseData<>(HttpStatus.OK.value(), "Get info order successfully", orderService.getOrder(orderId));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get detail info order failed");
        }
    }

    @Operation(summary = "Get list order")
    @GetMapping("/list")
    public ResponseData<?> getAllOrder(Pageable pageable,
                                       @RequestParam(required = false) String[] order,
                                       @RequestParam(required = false) String[] user){
        log.info("Get list order");
        try{
            return new ResponseData<>(HttpStatus.OK.value(), "Get list order successfully", orderService.getAllOrderForAdmin(pageable, order, user));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get list order failed");
        }
    }

    @Operation(summary = "Update order status")
    @PatchMapping("/status/{orderId}")
    public ResponseData<?> updateStatus(@Min(1) @PathVariable long orderId, @RequestParam OrderStatus status){
        log.info("Update order status");
        try {
            orderService.updateOrderStatus(orderId, status);
            return new ResponseData<>(HttpStatus.OK.value(), "Update order status successfully");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update order status failed");
        }
    }

    @Operation(summary = "Delete order")
    @DeleteMapping("/{orderId}")
    public ResponseData<?> deleteOrder(@Min(1) @PathVariable long orderId){
        log.info("Update order");
        try {
            orderService.deleteOrder(orderId);
            return new ResponseData<>(HttpStatus.OK.value(), "Delete order successfully");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete order failed");
        }
    }
}
