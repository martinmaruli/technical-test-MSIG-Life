package com.martin.payment.service;

import com.martin.payment.entity.Payment;
import com.martin.payment.entity.PaymentStatus;
import com.martin.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Payment createPayment(String orderId, String paymentRef, BigDecimal amount) {
        return repository.findByPaymentRef(paymentRef)
                .orElseGet(() -> {
                    Payment p = new Payment();
                    p.setOrderId(orderId);
                    p.setPaymentRef(paymentRef);
                    p.setAmount(amount);
                    p.setStatus(PaymentStatus.INITIATED);
                    p.setCreatedAt(LocalDateTime.now());
                    return repository.save(p);
                });
    }
}
