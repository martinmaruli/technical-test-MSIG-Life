package com.martin.order.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.martin.order.entity.Order;
import com.martin.order.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public void markPaid(String orderId) {
        Order order = repository.findById(orderId)
                .orElse(new Order(orderId, "PAID"));
        order.setStatus("PAID");
        repository.save(order);
    }

    public void markFailed(String orderId) {
        Order order = repository.findById(orderId)
                .orElse(new Order(orderId, "FAILED"));
        order.setStatus("FAILED");
        repository.save(order);
    }

    public List<Order> findAll() {
        return repository.findAll();
    }
}
