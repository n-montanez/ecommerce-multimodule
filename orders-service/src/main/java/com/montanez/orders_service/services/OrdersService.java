package com.montanez.orders_service.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.montanez.orders_service.client.PricingClient;
import com.montanez.orders_service.data.OrdersDao;
import com.montanez.orders_service.model.order.Order;
import com.montanez.orders_service.model.order.OrderItem;
import com.montanez.orders_service.model.order.OrderItemId;
import com.montanez.orders_service.model.order.OrderState;
import com.montanez.orders_service.model.order.dto.CreateOrderDTO;
import com.montanez.orders_service.model.order.dto.OrderInfoDTO;
import com.montanez.orders_service.model.order.dto.OrderItemRequest;
import com.montanez.orders_service.model.product.Product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

@Named
@ApplicationScoped
public class OrdersService {

    @Inject
    OrdersDao ordersDao;

    @Inject
    @RestClient
    private PricingClient pricingClient;

    public List<Order> getAllOrders() {
        return ordersDao.readAllOrders();
    }

    public OrderInfoDTO getOrderById(JsonWebToken token, UUID id) {
        Order order = ordersDao.readOrder(id);
        Long authId = Long.parseLong(token.getSubject());
        Set<String> roles = token.getGroups();

        if (order == null)
            throw new NotFoundException("Order not found");

        if (!roles.contains("admin") && order.getCustomer() != authId)
            throw new ForbiddenException("User does not have access to this order");

        List<OrderItemRequest> dtoItems = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            dtoItems.add(OrderItemRequest.builder()
                    .productId(item.getId().getProductId())
                    .quantity(item.getQuantity())
                    .build());
        }

        return OrderInfoDTO.builder()
                .id(id)
                .customer(authId)
                .date(order.getDate())
                .state(order.getState())
                .total(order.getTotal())
                .items(dtoItems)
                .build();
    }

    public OrderInfoDTO createOrder(JsonWebToken token, CreateOrderDTO createOrder) {
        Long authId = Long.parseLong(token.getSubject());

        if (createOrder.getItems() == null || createOrder.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        BigDecimal calculatedTotal = BigDecimal.ZERO;

        for (OrderItemRequest item : createOrder.getItems()) {
            Product product = pricingClient.getProductById(item.getProductId());
            calculatedTotal = calculatedTotal
                    .add(BigDecimal.valueOf(product.getPrice()))
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
        }

        Order order = Order.builder()
                .customer(authId)
                .date(LocalDate.now())
                .total(calculatedTotal)
                .state(OrderState.CREATED)
                .build();

        ordersDao.createOrder(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : createOrder.getItems()) {
            OrderItemId orderItemId = OrderItemId.builder()
                    .orderId(order.getId())
                    .productId(itemRequest.getProductId())
                    .build();

            OrderItem orderItem = OrderItem.builder()
                    .id(orderItemId)
                    .order(order)
                    .quantity(itemRequest.getQuantity())
                    .build();

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        ordersDao.updateOrder(order);

        return OrderInfoDTO.builder()
                .id(order.getId())
                .customer(order.getCustomer())
                .date(order.getDate())
                .total(order.getTotal())
                .state(order.getState())
                .items(createOrder.getItems())
                .build();
    }

    public List<OrderInfoDTO> getOrdersByUser(JsonWebToken token) {
        Long authId = Long.parseLong(token.getSubject());

        List<Order> orders = ordersDao.readOrderFromUser(authId);
        List<OrderInfoDTO> result = new ArrayList<>();

        for (Order order : orders) {
            OrderInfoDTO info = OrderInfoDTO.builder()
                    .id(order.getId())
                    .customer(authId)
                    .date(order.getDate())
                    .total(order.getTotal())
                    .state(order.getState())
                    .build();

            List<OrderItemRequest> finalItems = new ArrayList<>();
            for (OrderItem item : order.getItems()) {
                finalItems.add(OrderItemRequest.builder()
                        .productId(item.getId().getProductId())
                        .quantity(item.getQuantity())
                        .build());
            }

            info.setItems(finalItems);
            result.add(info);
        }

        return result;
    }
}
