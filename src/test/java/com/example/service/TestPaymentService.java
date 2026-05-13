package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.controller.PaymentValidationException;
import com.example.domain.Payment;
import com.example.domain.dto.PaymentDTO;

@SpringBootTest
class TestPaymentService {

    @Autowired
    private PaymentService paymentService;

    @Test
    void insert_shouldSavePayment() {
        PaymentDTO paymentDTO = createDTO();
        Payment result = paymentService.insert(paymentDTO);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("CREATED", result.getStatus());
    }

    @Test
    void findById_shouldReturnPayment() {
        Payment saved = paymentService.insert(createDTO());
        Payment found = paymentService.findById(saved.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void findById_shouldThrowExceptionWhenNotFound() {
        assertThrows(PaymentValidationException.class, () -> {
            paymentService.findById(UUID.randomUUID());
        });
    }

    @Test
    void update_shouldChangeFields() {
        Payment saved = paymentService.insert(createDTO());
        saved.setAmount(500.0);
        
        Payment updated = paymentService.update(saved);
        
        assertEquals(500.0, updated.getAmount());
    }

    @Test
    void completePayment_shouldChangeStatus() {
        Payment saved = paymentService.insert(createDTO());
        Payment completed = paymentService.completePayment(saved.getId());

        assertEquals("COMPLETED", completed.getStatus());
    }

    @Test
    void completePayment_shouldThrowExceptionOnInvalidTransition() {
        Payment saved = paymentService.insert(createDTO());
        paymentService.completePayment(saved.getId());

        assertThrows(PaymentValidationException.class, () -> {
            paymentService.failPayment(saved.getId());
        });
    }

    @Test
    void deletePayment_shouldRemovePayment() {
        Payment saved = paymentService.insert(createDTO());
        paymentService.deletePayment(saved.getId());

        assertThrows(PaymentValidationException.class, () -> {
            paymentService.findById(saved.getId());
        });
    }

    private PaymentDTO createDTO() {
        return PaymentDTO.builder()
                .amount(100.0)
                .currency("USD")
                .debtorAccount("acc1")
                .creditorAccount("acc2")
                .build();
    }
}
