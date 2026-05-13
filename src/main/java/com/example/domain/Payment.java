package com.example.domain;

import java.time.LocalDate;
import java.util.UUID;

import com.example.domain.dto.PaymentDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PAYMENT")
@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Payment {
        @Id
        private UUID id;
        private double amount;
        private String currency;
        private String debtorAccount;
        private String creditorAccount;
        private String status;
        private LocalDate createdAt;

        public Payment(PaymentDTO paymentDTO) {
                this.amount = paymentDTO.getAmount();
                this.currency = paymentDTO.getCurrency();
                this.debtorAccount = paymentDTO.getDebtorAccount();
                this.creditorAccount = paymentDTO.getCreditorAccount();
        }
}