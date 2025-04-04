package com.devdynamo.dtos.response;

import com.devdynamo.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderStatusCount {
    private OrderStatus status;
    private Long count;

    public OrderStatusCount(OrderStatus status, Long count) {
        this.status = status;
        this.count = count;
    }
}
