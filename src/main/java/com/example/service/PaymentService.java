package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.domain.Payment;
import com.example.repository.PaymentRepository;

@Service
public class PaymentService {
    private PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment insert(Payment payment) {
        return paymentRepository.save(payment);
    }
}