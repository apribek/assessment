package com.example.domain.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.domain.Payment;

@Component
public class PaymentValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!(target instanceof Payment payment)) {
            return;
        }
        if (idIsNull(payment)) {
            errors.rejectValue("id", "id.notPresent", "Id is required");
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
        if (!statusIsPresent(payment)) {
            errors.rejectValue("status", "status.notPresent", "Status is required");
        }
        if (!createdAtIsPresent(payment)) {
            errors.rejectValue("createdAt", "createdAt.notPresent", "Created at is required");
        }
    }

    public boolean idIsNull(Payment payment) {
        return Objects.isNull(payment.getId());
    }

    public boolean amountIsPositive(Payment payment) {
        return payment.getAmount() > 0;
    }

    public boolean currencyIsPresent(Payment payment) {
        return payment.getCurrency() != null && !payment.getCurrency().isBlank();
    }

    public boolean debtorAccountIsPresent(Payment payment) {
        return payment.getDebtorAccount() != null && !payment.getDebtorAccount().isBlank();
    }

    public boolean creditorAccountIsPresent(Payment payment) {
        return payment.getCreditorAccount() != null && !payment.getCreditorAccount().isBlank();
    }

    public boolean statusIsPresent(Payment payment) {
        return payment.getStatus() != null && !payment.getStatus().isBlank();
    }

    public boolean createdAtIsPresent(Payment payment) {
        return payment.getCreatedAt() != null;
    }

    public List<String> validateBeforeUpdate(Payment payment, Payment persistentPayment) {
        List<String> errorMessages = new ArrayList<>();

        if (!Objects.equals(payment.getStatus(), persistentPayment.getStatus())) {
            if (!isValidStatusTransition(persistentPayment.getStatus(), payment.getStatus())) {
                errorMessages.add("status.notChanged: Invalid status transition");
            }
        }
        if (payment.getStatus() != null && !Objects.equals(payment.getStatus(), persistentPayment.getStatus())) {
            errorMessages.add("status.notChanged: Status cannot be updated via PUT");
        }

        if (payment.getCreatedAt() != null && !Objects.equals(payment.getCreatedAt(), persistentPayment.getCreatedAt())) {
            errorMessages.add("createdAt.notChanged: Created at cannot be changed");
        }

        if (!isChangeAllowed(persistentPayment.getStatus())) {
            if (!Objects.equals(payment.getAmount(), persistentPayment.getAmount())) {
                errorMessages.add("amount.cantBeChanged: Payment cannot be changed");
            }
            if (!Objects.equals(payment.getCurrency(), persistentPayment.getCurrency())) {
                errorMessages.add("currency.cantBeChanged: Payment cannot be changed");
            }
            if (!Objects.equals(payment.getDebtorAccount(), persistentPayment.getDebtorAccount())) {
                errorMessages.add("debtorAccount.cantBeChanged: Payment cannot be changed");
            }
            if (!Objects.equals(payment.getCreditorAccount(), persistentPayment.getCreditorAccount())) {
                errorMessages.add("creditorAccount.cantBeChanged: Payment cannot be changed");
            }
        }
        return errorMessages;
    }

    public boolean isValidStatusTransition(String from, String to) {
        return switch (from) {
            case "CREATED" -> to.equals("COMPLETED") || to.equals("FAILED");
            default -> false;
        };
    }

    private boolean isChangeAllowed(String status) {
        return status.equals("CREATED");
    }

    public List<String> validateBeforeDelete(Payment payment) {
        List<String> errorMessages = new ArrayList<>();
        if (!isDeletable(payment.getStatus())) {
            errorMessages.add("status.notDeletable: Payment cannot be deleted");
        }
        return errorMessages;
    }

    private boolean isDeletable(String status) {
        return status.equals("CREATED");
    }
}
