package com.example.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "PAYMENT")
public record Payment(
        @Id Long id,
        double amount,
        String currency,
        String debtorAccount,
        String creditorAccount,
        String status,
        Date createdAt) {
}