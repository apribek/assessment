package com.example.domain.validator;

import static org.junit.jupiter.api.Assertions.*;
import com.example.domain.dto.PaymentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

class TestPaymentDTOValidator {

    private PaymentDTOValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PaymentDTOValidator();
    }

    @Test
    void supports_shouldReturnTrueForPaymentDTO() {
        assertTrue(validator.supports(PaymentDTO.class));
    }

    @Test
    void supports_shouldReturnTrueForOtherClasses() {
        assertTrue(validator.supports(Object.class));
    }

    @Test
    void validate_shouldHaveNoErrorsForValidPayment() {
        PaymentDTO payment = PaymentDTO.builder()
                .amount(100.0)
                .currency("USD")
                .debtorAccount("ACC1")
                .creditorAccount("ACC2")
                .build();
        Errors errors = new BeanPropertyBindingResult(payment, "payment");

        validator.validate(payment, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    void validate_shouldHaveErrorsWhenAmountIsNotPositive() {
        PaymentDTO payment = PaymentDTO.builder()
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
        PaymentDTO payment = PaymentDTO.builder()
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
        PaymentDTO payment = PaymentDTO.builder()
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
        PaymentDTO payment = PaymentDTO.builder()
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
        PaymentDTO payment = PaymentDTO.builder().amount(1.0).build();
        assertTrue(validator.amountIsPositive(payment));
    }

    @Test
    void amountIsPositive_shouldReturnFalseForZeroOrNegative() {
        assertFalse(validator.amountIsPositive(PaymentDTO.builder().amount(0).build()));
        assertFalse(validator.amountIsPositive(PaymentDTO.builder().amount(-10).build()));
    }

    @Test
    void currencyIsPresent_shouldReturnTrueWhenPresent() {
        assertTrue(validator.currencyIsPresent(PaymentDTO.builder().currency("USD").build()));
    }

    @Test
    void currencyIsPresent_shouldReturnFalseWhenMissingOrBlank() {
        assertFalse(validator.currencyIsPresent(PaymentDTO.builder().build()));
        assertFalse(validator.currencyIsPresent(PaymentDTO.builder().currency("").build()));
        assertFalse(validator.currencyIsPresent(PaymentDTO.builder().currency("  ").build()));
    }
}
