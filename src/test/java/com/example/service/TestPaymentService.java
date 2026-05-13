package com.example.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.domain.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Date;
import java.util.List;

@SpringBootTest
class TestPaymentService {

    @Autowired
    private PaymentService paymentService;

    @Test
    void insert_shouldSavePaymentInH2() {
        // Arrange
        Payment payment = Payment.builder()
                .id(1L)
                .amount(100.0)
                .currency("USD")
                .debtorAccount("acc1")
                .creditorAccount("acc2")
                .status("NEW")
                .createdAt(new java.sql.Date(new Date().getTime()))
                .build();

        // Act
        Payment result = paymentService.insert(payment);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());

        // Verify it's actually in the database
        List<Payment> allPayments = paymentService.findAll();
        assertTrue(allPayments.stream().anyMatch(p -> p.getId().equals(1L)));
    }
}
