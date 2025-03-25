package com.devdynamo.mappers;

import com.devdynamo.dtos.response.OrderItemResponseDTO;
import com.devdynamo.dtos.response.OrderResponseDTO;
import com.devdynamo.entities.OrderEntity;
import com.devdynamo.entities.OrderItemEntity;
import com.devdynamo.repositories.OrderItemRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public abstract class OrderMapper {
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Mappings({
            @Mapping(source = "user.fullName", target = "fullName"),
            @Mapping(source = "user.address", target = "address"),
            @Mapping(source = "payment.status", target = "paymentStatus")
    })
    public abstract OrderResponseDTO toDTO(OrderEntity entity);

    public OrderResponseDTO toDTOWithItems(OrderEntity entity) {
        List<OrderItemEntity> orderItems = orderItemRepository.findAllByOrderId(entity.getId());
        List<OrderItemResponseDTO> orderItemDTOs = orderItems.stream().map(orderItemMapper::toDTO)
                .collect(Collectors.toList());

        OrderResponseDTO dto = toDTO(entity);
        dto.setProducts(orderItemDTOs);
        return dto;
    }
}
