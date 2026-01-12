package com.martin.notification.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @PostMapping("/payment-success/{orderId}")
    public void notifySuccess(@PathVariable String orderId) {
        System.out.println("Notification sent for order: " + orderId);
    }
}
