package com.montanez.orders_service.model.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "c_order")
@NamedQuery(name = "Order.findAll", query = "SELECT o FROM Order o")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "customer_id")
    private Long customer;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "total")
    @Min(value = 0, message = "Order total price cannot be negative")
    private BigDecimal total;

    @Default
    @Column(name = "order_state")
    @Enumerated(EnumType.STRING)
    private OrderState state = OrderState.CREATED;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;
}
