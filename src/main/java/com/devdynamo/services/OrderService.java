package com.devdynamo.services;

import com.devdynamo.dtos.request.OrderRequestDTO;
import com.devdynamo.dtos.response.OrderResponseDTO;
import com.devdynamo.dtos.response.PageResponse;
import com.devdynamo.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDTO getOrder(long orderId);

    PageResponse<?> getAllOrderForAdmin(Pageable pageable, String[] order, String[] user);

    PageResponse<?> getAllOrderForClient(int pageNo, int pageSize, long userId);

    void cancelOrder(long orderId);

    void createOrder(OrderRequestDTO request);

    void updateOrderStatus(long orderId, OrderStatus status);

    void deleteOrder(long orderId);
}
