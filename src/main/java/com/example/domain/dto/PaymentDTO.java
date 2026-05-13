package com.example.domain.dto;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class PaymentDTO {
        private double amount;
        private String currency;
        private String debtorAccount;
        private String creditorAccount;
}