package com.devdynamo.dtos.response;

import com.devdynamo.enums.OrderStatus;
import com.devdynamo.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderResponseDTO implements Serializable {
    private Long id;
    private String orderCode;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private String fullName;
    private String address;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponseDTO> products;
}
