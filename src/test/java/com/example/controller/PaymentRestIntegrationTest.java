package com.example.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.domain.Payment;
import com.example.domain.dto.PaymentDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentRestIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testFullPaymentLifecycle() {
        // 1. Create Payment
        PaymentDTO createDto = PaymentDTO.builder()
                .amount(100.0)
                .currency("EUR")
                .debtorAccount("DE123")
                .creditorAccount("DE456")
                .build();

        ResponseEntity<Payment> createResponse = restTemplate.postForEntity("/payments", createDto, Payment.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Payment payment = createResponse.getBody();
        assertThat(payment).isNotNull();
        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getStatus()).isEqualTo("CREATED");
        UUID id = payment.getId();

        // 2. Get Payment
        ResponseEntity<Payment> getResponse = restTemplate.getForEntity("/payments/" + id, Payment.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getAmount()).isEqualTo(100.0);

        // 3. Update Payment
        payment.setAmount(150.0);
        HttpEntity<Payment> updateRequest = new HttpEntity<>(payment);
        ResponseEntity<Payment> updateResponse = restTemplate.exchange("/payments/" + id, HttpMethod.PUT, updateRequest, Payment.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().getAmount()).isEqualTo(150.0);

        // 4. Complete Payment
        ResponseEntity<Payment> completeResponse = restTemplate.postForEntity("/payments/" + id + "/complete", null, Payment.class);
        assertThat(completeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(completeResponse.getBody().getStatus()).isEqualTo("COMPLETED");

        // 5. Try to delete Completed Payment (Should Fail)
        ResponseEntity<Void> deleteResponse = restTemplate.exchange("/payments/" + id, HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // 6. Verify it still exists
        getResponse = restTemplate.getForEntity("/payments/" + id, Payment.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testInvalidCreation() {
        PaymentDTO invalidDto = PaymentDTO.builder()
                .amount(-10.0) // Invalid amount
                .currency("EUR")
                .debtorAccount("DE123")
                .creditorAccount("DE456")
                .build();

        ResponseEntity<Object> response = restTemplate.postForEntity("/payments", invalidDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testInvalidStatusTransition() {
        // Create
        PaymentDTO createDto = PaymentDTO.builder()
                .amount(100.0)
                .currency("EUR")
                .debtorAccount("DE1")
                .creditorAccount("DE2")
                .build();
        Payment payment = restTemplate.postForEntity("/payments", createDto, Payment.class).getBody();
        
        // Fail it
        restTemplate.postForEntity("/payments/" + payment.getId() + "/fail", null, Payment.class);
        
        // Try to complete it (Invalid from FAILED)
        ResponseEntity<Payment> response = restTemplate.postForEntity("/payments/" + payment.getId() + "/complete", null, Payment.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
