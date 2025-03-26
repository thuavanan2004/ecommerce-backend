package com.devdynamo.services.Impl;

import com.devdynamo.dtos.request.OrderItemRequestDTO;
import com.devdynamo.dtos.request.OrderRequestDTO;
import com.devdynamo.dtos.response.OrderResponseDTO;
import com.devdynamo.dtos.response.PageResponse;
import com.devdynamo.entities.OrderEntity;
import com.devdynamo.entities.OrderItemEntity;
import com.devdynamo.entities.ProductEntity;
import com.devdynamo.entities.UserEntity;
import com.devdynamo.enums.OrderStatus;
import com.devdynamo.exceptions.ResourceNotFoundException;
import com.devdynamo.mappers.OrderMapper;
import com.devdynamo.repositories.*;
import com.devdynamo.repositories.specification.OrderSpecificationsBuilder;
import com.devdynamo.services.OrderService;
import com.devdynamo.utils.GenerateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.devdynamo.utils.AppConst.SEARCH_SPEC_OPERATOR;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final SearchRepository searchRepository;

    private final OrderMapper orderMapper;

    public ProductEntity getProduct(long productId){
        return productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public OrderResponseDTO getOrder(long orderId) {
        return orderMapper.toDTO(orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found")));
    }

    @Override
    public PageResponse<?> getAllOrderForAdmin(Pageable pageable, String[] order, String[] user) {
        Page<OrderEntity> orders;
        if(order != null && user != null){
            return searchRepository.searchOrderByCriteriaWithJoin(pageable, order, user);
        } else if(order != null){
            OrderSpecificationsBuilder builder = new OrderSpecificationsBuilder();
            Pattern pattern = Pattern.compile(SEARCH_SPEC_OPERATOR);
            for (String s : order) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }
            orders = orderRepository.findAll(Objects.requireNonNull(builder.build()), pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        List<OrderResponseDTO> list = orders.stream().map(orderMapper::toDTO).toList();
        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(orders.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public PageResponse<?> getAllOrderForClient(int pageNo, int pageSize, long userId) {
        int page = 0;
        if(pageNo > 0){
            page = pageNo - 1;
        }

        Pageable pageable = PageRequest.of( page, pageSize);

        Page<OrderEntity> list = orderRepository.findAllByUserId(userId, pageable);
        List<OrderResponseDTO> orders = list.stream().map(orderMapper::toDTOWithItems).toList();
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(list.getTotalPages())
                .items(orders)
                .build();
    }

    @Override
    public void cancelOrder(long orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(OrderStatus.cancelled);

        orderRepository.save(order);
        log.info("Cancel order successfully");
    }

    @Override
    public void createOrder(OrderRequestDTO request) {
        UserEntity user = userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User with userId not found"));

        String orderCode = GenerateUtils.generateOrderCode();

        BigDecimal totalPrice = request.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Long> productIds = request.getItems().stream().map(OrderItemRequestDTO::getProductId).toList();
        Map<Long, ProductEntity> productMap = productRepository.findAllById(productIds).stream().collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setOrderCode(orderCode);
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        List<OrderItemEntity> orderItems = request.getItems().stream().map(item -> {
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setProduct(productMap.get(item.getProductId()));
            orderItem.setPrice(item.getPrice());
            return orderItem;
        }).toList();

        orderItemRepository.saveAll(orderItems);

        log.info("Create new order successfully");
    }


    @Override
    public void updateOrderStatus(long orderId, OrderStatus status) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);

        log.info("Service: Update order status successfully");
    }

    @Override
    public void deleteOrder(long orderId) {
        orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        orderRepository.deleteById(orderId);

        log.info("Service: Delete order successfully");
    }
}
