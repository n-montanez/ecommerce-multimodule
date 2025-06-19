package com.montanez.orders_service.model.order.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.montanez.orders_service.model.order.OrderState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoDTO {
    private UUID id;
    private Long customer;
    private LocalDate date;
    private BigDecimal total;
    private OrderState state;
    private List<OrderItemRequest> items;
}
