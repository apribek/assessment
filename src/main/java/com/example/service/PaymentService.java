package com.example.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.domain.Payment;
import com.example.domain.dto.PaymentDTO;
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

    public Optional<Payment> findById(UUID id) {
        return paymentRepository.findById(id);
    }

    public Payment insert(PaymentDTO paymentDTO) {
        Payment payment = new Payment(paymentDTO);
        payment.setId(UUID.randomUUID());
        payment.setCreatedAt(new Date(new java.util.Date().getTime()));
        payment.setStatus("CREATED");
        return paymentRepository.save(payment);
    }
}