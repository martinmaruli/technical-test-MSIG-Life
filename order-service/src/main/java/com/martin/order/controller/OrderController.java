package com.martin.order.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.martin.order.entity.Order;
import com.martin.order.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<Order> getAll() {
        return service.findAll();
    }

    @PostMapping
    public Order create(@RequestBody Order order) {
        return service.createOrder(order.getOrderId());
    }

    @GetMapping("/{orderId}")
    public Order get(@PathVariable String orderId) {
        return service.getOrder(orderId);
    }

    @PostMapping("/{orderId}/paid")
    public void paid(@PathVariable String orderId) {
        service.markPaid(orderId);
    }

    @PostMapping("/{orderId}/failed")
    public void failed(@PathVariable String orderId) {
        service.markFailed(orderId);
    }
}
