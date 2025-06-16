package com.montanez.orders_service.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "customer_id")
    private UUID customer;

    @Column(name = "date")
    private Date date;

    @Column(name = "total")
    @Min(value = 0, message = "Order total price cannot be negative")
    private BigDecimal total;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private OrderState state;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;
}
