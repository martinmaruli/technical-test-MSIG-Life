package com.martin.order.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.martin.order.entity.Order;
import com.martin.order.entity.OrderStatus;
import com.martin.order.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public List<Order> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Order createOrder(String orderId) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.PENDING);
        return repository.save(order);
    }

    public Order getOrder(String orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional
    public void markPaid(String orderId) {
        Order order = repository.findById(orderId).orElseThrow();
        order.setStatus(OrderStatus.PAID);
        repository.save(order);
    }

    @Transactional
    public void markFailed(String orderId) {
        Order order = repository.findById(orderId).orElseThrow();
        order.setStatus(OrderStatus.FAILED);
        repository.save(order);
    }
}
