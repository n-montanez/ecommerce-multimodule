package com.montanez.orders_service.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import com.montanez.orders_service.model.order.Order;
import com.montanez.orders_service.model.order.OrderItem;
import com.montanez.orders_service.model.order.dto.OrderInfoDTO;
import com.montanez.orders_service.model.order.dto.OrderItemRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "items", target = "items")
    OrderInfoDTO toOrderInfoDTO(Order order);

    @Mapping(source = "id.productId", target = "productId")
    OrderItemRequest toOrderItemRequest(OrderItem orderItem);

    @Mapping(source = "productId", target = "id.productId")
    @Mapping(target = "id.orderId", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "quantity", source = "quantity")
    OrderItem toOrderItem(OrderItemRequest orderItemRequest);

    List<OrderItemRequest> toOrderItemRequests(List<OrderItem> orderItems);

    List<OrderItem> toOrderItemListFromRequest(List<OrderItemRequest> orderItemRequests);
}
