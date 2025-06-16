package com.montanez.orders_service.data;

import java.util.List;
import java.util.UUID;

import com.montanez.orders_service.model.Order;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RequestScoped
public class OrdersDao {
    @PersistenceContext(name = "orders-jpa-unit")
    EntityManager em;

    public void createOrder(Order order) {
        em.persist(order);
        em.flush();
    }

    public Order readOrder(UUID id) {
        return em.find(Order.class, id);
    }

    public void updateOrder(Order order) {
        em.merge(order);
    }

    public void deleteOrder(Order order) {
        em.remove(order);
    }

    public List<Order> readAllOrders() {
        return em.createNamedQuery("Order.findAll", Order.class).getResultList();
    }
}
