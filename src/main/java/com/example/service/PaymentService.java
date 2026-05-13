package com.example.service;

import java.time.LocalDate;
import java.util.List;
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

    public Payment findById(UUID id) {
        return paymentRepository
                .findById(id)
                .orElseThrow(() -> new PaymentValidationException("Not found"));
    }

    public Payment insert(PaymentDTO paymentDTO) {
        Payment payment = new Payment(paymentDTO);
        payment.setId(UUID.randomUUID());
        payment.setCreatedAt(LocalDate.now());
        payment.setStatus("CREATED");
        paymentRepository.save(payment);
        return payment;
    }

    public Payment update(Payment payment) {
        Payment persistentPayment = paymentRepository
                .findById(payment.getId())
                .orElseThrow(() -> new PaymentValidationException("Payment not found"));

        List<String> errorMessages = paymentValidator.validateBeforeUpdate(payment, persistentPayment);
        if (!errorMessages.isEmpty()) {
            System.out.println(errorMessages);
            throw new PaymentValidationException(String.join(", ", errorMessages));
        }

        persistentPayment.setAmount(payment.getAmount());
        persistentPayment.setCurrency(payment.getCurrency());
        persistentPayment.setDebtorAccount(payment.getDebtorAccount());
        persistentPayment.setCreditorAccount(payment.getCreditorAccount());
        persistentPayment.setStatus(payment.getStatus());
        paymentRepository.save(persistentPayment);

        return persistentPayment;
    }

    public Payment completePayment(UUID id) {
        return updateStatus(id, "COMPLETED");
    }

    public Payment failPayment(UUID id) {
        return updateStatus(id, "FAILED");
    }

    private Payment updateStatus(UUID id, String status) {
        Payment persistentPayment = paymentRepository
                .findById(id)
                .orElseThrow(() -> new PaymentValidationException("Payment not found"));
        if (!paymentValidator.isValidStatusTransition(persistentPayment.getStatus(), status)) {
            throw new PaymentValidationException("Invalid status transition");
        }
        persistentPayment.setStatus(status);
        paymentRepository.save(persistentPayment);
        return persistentPayment;
    }

    public void deletePayment(UUID id) {
        Payment payment = paymentRepository.findById(id).get();

        List<String> errorMessages = paymentValidator.validateBeforeDelete(payment);

        if (!errorMessages.isEmpty()) {
            System.out.println(errorMessages);
            throw new PaymentValidationException(String.join(", ", errorMessages));
        }
        paymentRepository.deleteById(id);
    }
}