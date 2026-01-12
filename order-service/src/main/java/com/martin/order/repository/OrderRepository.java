package com.martin.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.martin.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, String> {
}
