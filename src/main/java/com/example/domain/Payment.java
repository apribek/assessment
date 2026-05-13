package com.example.domain;

import java.sql.Date;

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
        private Long id;
        private double amount;
        private String currency;
        private String debtorAccount;
        private String creditorAccount;
        private String status;
        private Date createdAt;
}