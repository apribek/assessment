package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.domain.Payment;
import com.example.domain.dto.PaymentDTO;

@SpringBootTest
class TestPaymentService {

    @Autowired
    private PaymentService paymentService;

    @Test
    void insert_shouldSavePayment() {
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .amount(100.0)
                .currency("USD")
                .debtorAccount("acc1")
                .creditorAccount("acc2")
                .build();

        Payment result = paymentService.insert(paymentDTO);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(100.0, result.getAmount());
        assertEquals("USD", result.getCurrency());
        assertEquals("acc1", result.getDebtorAccount());
        assertEquals("acc2", result.getCreditorAccount());
        assertEquals("CREATED", result.getStatus());
        assertNotNull(result.getCreatedAt());
    }
}
