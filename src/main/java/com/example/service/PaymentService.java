package com.example.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.controller.PaymentValidationException;
import com.example.domain.Payment;
import com.example.domain.dto.PaymentDTO;
import com.example.domain.validator.PaymentValidator;
import com.example.repository.PaymentRepository;

@Service
public class PaymentService {
    private PaymentRepository paymentRepository;
    private PaymentValidator paymentValidator;

    public PaymentService(PaymentRepository paymentRepository, PaymentValidator paymentValidator) {
        this.paymentRepository = paymentRepository;
        this.paymentValidator = paymentValidator;
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

    public Payment update(Payment payment) {
        Payment persistentPayment = paymentRepository.findById(payment.getId()).get();

        List<String> errorMessages = paymentValidator.validateBeforeUpdate(payment, persistentPayment);

        if (!errorMessages.isEmpty()) {
            System.out.println(errorMessages);
            throw new PaymentValidationException(String.join(", ", errorMessages));
        }
        return paymentRepository.save(payment);
    }

}