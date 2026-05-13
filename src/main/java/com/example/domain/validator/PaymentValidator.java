package com.example.domain.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.domain.dto.PaymentDTO;

@Component
public class PaymentValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PaymentDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!(target instanceof PaymentDTO payment)) {
            errors.rejectValue("payment", "payment.invalid", "Invalid dto type");
            return;
        }
        if (!amountIsPositive(payment)) {
            errors.rejectValue("amount", "amount.notPositive", "Amount must be positive");
        }
        if (!currencyIsPresent(payment)) {
            errors.rejectValue("currency", "currency.notPresent", "Currency is required");
        }
        if (!debtorAccountIsPresent(payment)) {
            errors.rejectValue("debtorAccount", "debtorAccount.notPresent", "Debtor account is required");
        }
        if (!creditorAccountIsPresent(payment)) {
            errors.rejectValue("creditorAccount", "creditorAccount.notPresent", "Creditor account is required");
        }
    }

    public boolean amountIsPositive(PaymentDTO payment) {
        return payment.getAmount() > 0;
    }

    public boolean currencyIsPresent(PaymentDTO payment) {
        return payment.getCurrency() != null && !payment.getCurrency().isBlank();
    }

    public boolean debtorAccountIsPresent(PaymentDTO payment) {
        return payment.getDebtorAccount() != null && !payment.getDebtorAccount().isBlank();
    }

    public boolean creditorAccountIsPresent(PaymentDTO payment) {
        return payment.getCreditorAccount() != null && !payment.getCreditorAccount().isBlank();
    }
}
