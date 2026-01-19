package com.martin.payment.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.martin.payment.client.NotificationClient;
import com.martin.payment.client.OrderClient;
import com.martin.payment.entity.Payment;
import com.martin.payment.entity.PaymentStatus;
import com.martin.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository repository;
    private final OrderClient orderClient;
    private final NotificationClient notificationClient;

    public PaymentService(
            PaymentRepository repository,
            OrderClient orderClient,
            NotificationClient notificationClient) {
        this.repository = repository;
        this.orderClient = orderClient;
        this.notificationClient = notificationClient;
    }

    @Transactional
    public Payment create(Payment request) {
        Payment p = new Payment();
        p.setPaymentRef(UUID.randomUUID().toString());
        p.setOrderId(request.getOrderId());
        p.setAmount(request.getAmount());
        p.setStatus(PaymentStatus.PENDING);
        p.setCreatedAt(LocalDateTime.now());
        return repository.save(p);
    }

    @Transactional
    public void callback(String paymentRef, String status, String txnId) {
        Payment p = repository.findByPaymentRef(paymentRef).orElseThrow();

        if (p.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        if ("SUCCESS".equalsIgnoreCase(status)) {
            p.setStatus(PaymentStatus.SUCCESS);
            p.setGatewayTransactionId(txnId);
            orderClient.markPaid(p.getOrderId());
            notificationClient.notifySuccess(p.getOrderId());
        } else {
            p.setStatus(PaymentStatus.FAILED);
            orderClient.markFailed(p.getOrderId());
        }

        p.setUpdatedAt(LocalDateTime.now());
        repository.save(p);
    }
}
