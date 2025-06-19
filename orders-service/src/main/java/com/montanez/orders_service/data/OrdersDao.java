package com.montanez.orders_service.data;

import java.util.List;
import java.util.UUID;

import com.montanez.orders_service.model.order.Order;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@RequestScoped
@Transactional
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

    public List<Order> readOrderFromUser(Long userId) {
        return em.createNamedQuery("Order.findByUser", Order.class)
                .setParameter("customer", userId)
                .getResultList();
    }
}
