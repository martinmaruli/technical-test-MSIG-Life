package com.martin.payment.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.martin.payment.client.NotificationClient;
import com.martin.payment.client.OrderClient;
import com.martin.payment.entity.Payment;
import com.martin.payment.entity.PaymentStatus;
import com.martin.payment.repository.PaymentRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentRepository repository;
    private final OrderClient orderClient;
    private final NotificationClient notificationClient;

    public PaymentController(
            PaymentRepository repository,
            OrderClient orderClient,
            NotificationClient notificationClient
    ) {
        this.repository = repository;
        this.orderClient = orderClient;
        this.notificationClient = notificationClient;
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment request) {

        Payment payment = new Payment();
        payment.setPaymentRef(UUID.randomUUID().toString());
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());

        repository.save(payment);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/callback")
    @Transactional
    public ResponseEntity<String> paymentCallback(
            @RequestParam String paymentRef,
            @RequestParam String status,
            @RequestParam(required = false) String gatewayTransactionId
    ) {
        Payment payment = repository.findByPaymentRef(paymentRef)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return ResponseEntity.ok("Already processed");
        }

        if ("SUCCESS".equalsIgnoreCase(status)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setGatewayTransactionId(gatewayTransactionId);

            orderClient.markPaid(payment.getOrderId());

            notificationClient.notifySuccess(payment.getOrderId());

        } else {
            payment.setStatus(PaymentStatus.FAILED);

            orderClient.markFailed(payment.getOrderId());
        }

        payment.setUpdatedAt(LocalDateTime.now());
        repository.save(payment);

        return ResponseEntity.ok("Callback handled");
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return repository.findAll();
    }

    @GetMapping("/{paymentRef}")
    public Payment getByPaymentRef(@PathVariable String paymentRef) {
        return repository.findByPaymentRef(paymentRef)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}
