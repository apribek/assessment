package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.Payment;
import com.example.domain.dto.PaymentDTO;
import com.example.domain.validator.PaymentDTOValidator;
import com.example.domain.validator.PaymentValidator;
import com.example.service.PaymentService;

@RestController
class PaymentController {

  private PaymentService paymentService;
  private PaymentDTOValidator paymentDTOValidator;
  private PaymentValidator paymentValidator;

  PaymentController(PaymentService paymentService, PaymentDTOValidator paymentDTOValidator,
      PaymentValidator paymentValidator) {
    this.paymentService = paymentService;
    this.paymentDTOValidator = paymentDTOValidator;
    this.paymentValidator = paymentValidator;
  }

  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(paymentValidator);
    binder.addValidators(paymentDTOValidator);
  }

  @PostMapping("/payments")
  Payment newPayment(@Validated @RequestBody PaymentDTO newPayment) {
    return paymentService.insert(newPayment);
  }

  @GetMapping("/payments")
  List<Payment> all() {
    return paymentService.findAll();
  }

  @GetMapping("/payments/{id}")
  Payment getPayment(@PathVariable java.util.UUID id) {

    return paymentService.findById(id)
        .orElseThrow(() -> new PaymentNotFoundException(id));
  }

  @org.springframework.web.bind.annotation.PutMapping("/payments/{id}")
  ResponseEntity<Payment> updatePayment(@PathVariable java.util.UUID id, @Validated @RequestBody Payment payment) {
    payment.setId(id);
    try {
      return ResponseEntity.ok(paymentService.update(payment));
    } catch (PaymentValidationException e) {
      return ResponseEntity.status(400).body(payment);
    }
  }
}