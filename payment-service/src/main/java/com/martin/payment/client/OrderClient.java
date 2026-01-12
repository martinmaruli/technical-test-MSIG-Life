package com.martin.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "order-service", url = "${order.service.url}")
public interface OrderClient {

    @PostMapping("/orders/{orderId}/paid")
    void markPaid(@PathVariable String orderId);

    @PostMapping("/orders/{orderId}/failed")
    void markFailed(@PathVariable String orderId);
}
