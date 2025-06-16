package com.montanez.orders_service.services;

import com.montanez.orders_service.data.OrdersDao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrdersService {
    
    @Inject
    OrdersDao ordersDao;

}
