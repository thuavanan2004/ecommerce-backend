package com.devdynamo.controllers.client;

import com.devdynamo.dtos.request.OrderRequestDTO;
import com.devdynamo.dtos.response.ResponseData;
import com.devdynamo.dtos.response.ResponseError;
import com.devdynamo.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/orders")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Client orders")
public class ClientOrderController {
    private final OrderService orderService;

    @Operation(summary = "Get list order")
    @GetMapping("/list/{userId}")
    public ResponseData<?> getAllOrderForClient(@RequestParam(defaultValue = "0") int pageNo,
                                                @RequestParam(defaultValue = "10") int pageSize,
                                                @Min(1) @PathVariable long userId){
        log.info("Get list order");
        try{
            return new ResponseData<>(HttpStatus.OK.value(), "Get list order successfully", orderService.getAllOrderForClient(pageNo, pageSize, userId));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get list order failed");
        }
    }

    @Operation(summary = "Create order")
    @PostMapping("/create")
    public ResponseData<?> createOrder(@Valid @RequestBody OrderRequestDTO request){
        log.info("Create order");
        try{
            orderService.createOrder(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Create order successfully");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Create order failed");
        }
    }

    @Operation(summary = "Cancel order")
    @PatchMapping("/cancel/{orderId}")
    public ResponseData<?> cancelOrder(@Min(1) @PathVariable long orderId){
           log.info("Cancel order");
        try{
            orderService.cancelOrder(orderId);
            return new ResponseData<>(HttpStatus.OK.value(), "Cancel order successfully");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Cancel order failed");
        }
    }
}
