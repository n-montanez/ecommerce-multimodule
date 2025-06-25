package com.montanez.orders_service.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.montanez.orders_service.model.payment.PaymentRequest;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.montanez.orders_service.client.PricingClient;
import com.montanez.orders_service.data.OrdersDao;
import com.montanez.orders_service.mappers.OrderMapper;
import com.montanez.orders_service.model.order.Order;
import com.montanez.orders_service.model.order.OrderItem;
import com.montanez.orders_service.model.order.OrderState;
import com.montanez.orders_service.model.order.dto.CreateOrderDTO;
import com.montanez.orders_service.model.order.dto.OrderInfoDTO;
import com.montanez.orders_service.model.order.dto.OrderItemRequest;
import com.montanez.orders_service.model.product.Product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class OrdersService {

    @Inject
    OrdersDao ordersDao;

    @Inject
    @RestClient
    private PricingClient pricingClient;

    @Inject
    private OrderMapper orderMapper;

    @Inject
    PublishToPayments sender;

    public List<OrderInfoDTO> getAllOrders() {
        return ordersDao
                .readAllOrders()
                .stream()
                .map(orderMapper::toOrderInfoDTO)
                .collect(Collectors.toList());
    }

    public OrderInfoDTO getOrderById(JsonWebToken token, UUID id) {
        Order order = ordersDao.readOrder(id);
        Long authId = Long.parseLong(token.getSubject());
        Set<String> roles = token.getGroups();

        if (order == null)
            throw new NotFoundException("Order not found");

        if (!roles.contains("admin") && order.getCustomer() != authId)
            throw new ForbiddenException("User does not have access to this order");

        return orderMapper.toOrderInfoDTO(order);
    }

    public OrderInfoDTO createOrder(JsonWebToken token, CreateOrderDTO createOrder) {
        Long authId = Long.parseLong(token.getSubject());

        if (createOrder.getItems() == null || createOrder.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        BigDecimal calculatedTotal = BigDecimal.ZERO;

        Order order = Order.builder()
                .customer(authId)
                .date(LocalDate.now())
                .state(OrderState.CREATED)
                .build();

        ordersDao.createOrder(order);

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest item : createOrder.getItems()) {
            Product product = pricingClient.getProductById(item.getProductId());
            calculatedTotal = calculatedTotal
                    .add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            OrderItem orderItem = orderMapper.toOrderItem(item);
            orderItem.getId().setOrderId(order.getId());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }

        order.setTotal(calculatedTotal);
        order.setItems(orderItems);
        ordersDao.updateOrder(order);

        sender.sendPaymentRequest(new PaymentRequest(order.getId(), order.getTotal()));

        return orderMapper.toOrderInfoDTO(order);
    }

    public List<OrderInfoDTO> getOrdersByUser(JsonWebToken token) {
        Long authId = Long.parseLong(token.getSubject());
        List<Order> orders = ordersDao.readOrderFromUser(authId);

        return orders.stream()
                .map(orderMapper::toOrderInfoDTO)
                .collect(Collectors.toList());
    }
}
