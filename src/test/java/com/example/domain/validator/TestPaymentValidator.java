package com.example.domain.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import com.example.domain.Payment;

class TestPaymentValidator {

    private PaymentValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PaymentValidator();
    }

    @Test
    void supports_shouldReturnTrueForPayment() {
        assertTrue(validator.supports(Payment.class));
    }

    @Test
    void supports_shouldReturnTrueForOtherClasses() {
        assertTrue(validator.supports(Object.class));
    }

    @Test
    void validate_shouldHaveNoErrorsForValidPayment() {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .amount(100.0)
                .currency("USD")
                .debtorAccount("ACC1")
                .creditorAccount("ACC2")
                .status("CREATED")
                .createdAt(java.time.LocalDate.now())
                .build();
        Errors errors = new BeanPropertyBindingResult(payment, "payment");

        validator.validate(payment, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    void validate_shouldHaveErrorsWhenAmountIsNotPositive() {
        Payment payment = Payment.builder()
                .amount(-1.0)
                .currency("USD")
                .debtorAccount("ACC1")
                .creditorAccount("ACC2")
                .build();
        Errors errors = new BeanPropertyBindingResult(payment, "payment");

        validator.validate(payment, errors);

        assertTrue(errors.hasFieldErrors("amount"));
        assertEquals("amount.notPositive", errors.getFieldError("amount").getCode());
    }

    @Test
    void validate_shouldHaveErrorsWhenCurrencyIsMissing() {
        Payment payment = Payment.builder()
                .amount(100.0)
                .debtorAccount("ACC1")
                .creditorAccount("ACC2")
                .build();
        Errors errors = new BeanPropertyBindingResult(payment, "payment");

        validator.validate(payment, errors);

        assertTrue(errors.hasFieldErrors("currency"));
        assertEquals("currency.notPresent", errors.getFieldError("currency").getCode());
    }

    @Test
    void validate_shouldHaveErrorsWhenDebtorAccountIsMissing() {
        Payment payment = Payment.builder()
                .amount(100.0)
                .currency("USD")
                .creditorAccount("ACC2")
                .build();
        Errors errors = new BeanPropertyBindingResult(payment, "payment");

        validator.validate(payment, errors);

        assertTrue(errors.hasFieldErrors("debtorAccount"));
        assertEquals("debtorAccount.notPresent", errors.getFieldError("debtorAccount").getCode());
    }

    @Test
    void validate_shouldHaveErrorsWhenCreditorAccountIsMissing() {
        Payment payment = Payment.builder()
                .amount(100.0)
                .currency("USD")
                .debtorAccount("ACC1")
                .build();
        Errors errors = new BeanPropertyBindingResult(payment, "payment");

        validator.validate(payment, errors);

        assertTrue(errors.hasFieldErrors("creditorAccount"));
        assertEquals("creditorAccount.notPresent", errors.getFieldError("creditorAccount").getCode());
    }

    @Test
    void amountIsPositive_shouldReturnTrueForPositiveAmount() {
        Payment payment = Payment.builder().amount(1.0).build();
        assertTrue(validator.amountIsPositive(payment));
    }

    @Test
    void amountIsPositive_shouldReturnFalseForZeroOrNegative() {
        assertFalse(validator.amountIsPositive(Payment.builder().amount(0).build()));
        assertFalse(validator.amountIsPositive(Payment.builder().amount(-10).build()));
    }

    @Test
    void currencyIsPresent_shouldReturnTrueWhenPresent() {
        assertTrue(validator.currencyIsPresent(Payment.builder().currency("USD").build()));
    }

    @Test
    void currencyIsPresent_shouldReturnFalseWhenMissingOrBlank() {
        assertFalse(validator.currencyIsPresent(Payment.builder().build()));
        assertFalse(validator.currencyIsPresent(Payment.builder().currency("").build()));
        assertFalse(validator.currencyIsPresent(Payment.builder().currency("  ").build()));
    }

    @Test
    void validateBeforeUpdate_shouldHaveNoErrorsForValidTransition() {
        java.time.LocalDate now = java.time.LocalDate.now();
        Payment persistent = Payment.builder().status("CREATED").createdAt(now).amount(100.0).build();
        Payment updated = Payment.builder().status("COMPLETED").createdAt(now).amount(100.0).build();

        java.util.List<String> errors = validator.validateBeforeUpdate(updated, persistent);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validateBeforeUpdate_shouldHaveErrorForInvalidStatusTransition() {
        java.time.LocalDate now = java.time.LocalDate.now();
        Payment persistent = Payment.builder().status("COMPLETED").createdAt(now).build();
        Payment updated = Payment.builder().status("FAILED").createdAt(now).build();

        java.util.List<String> errors = validator.validateBeforeUpdate(updated, persistent);

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("status.notChanged"));
    }

    @Test
    void validateBeforeUpdate_shouldHaveErrorWhenCreatedAtChanged() {
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDate later = now.plusDays(1);
        Payment persistent = Payment.builder().status("CREATED").createdAt(now).build();
        Payment updated = Payment.builder().status("CREATED").createdAt(later).build();

        java.util.List<String> errors = validator.validateBeforeUpdate(updated, persistent);

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("createdAt.notChanged"));
    }

    @Test
    void validateBeforeUpdate_shouldHaveErrorWhenAmountChangedInCompletedStatus() {
        java.time.LocalDate now = java.time.LocalDate.now();
        Payment persistent = Payment.builder().status("COMPLETED").createdAt(now).amount(100.0).build();
        Payment updated = Payment.builder().status("COMPLETED").createdAt(now).amount(200.0).build();

        java.util.List<String> errors = validator.validateBeforeUpdate(updated, persistent);

        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("amount.cantBeChanged")));
    }
}
