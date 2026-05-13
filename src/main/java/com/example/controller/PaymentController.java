package com.example.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.Payment;
import com.example.service.PaymentService;

@RestController
class PaymentController {

  private PaymentService paymentService;

  PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @GetMapping("/payments")
  List<Payment> all() {
    return paymentService.findAll();
  }

  @PostMapping("/payments")
  Payment newPayment(@RequestBody Payment newPayment) {
    return paymentService.insert(newPayment);
  }

  @GetMapping("/payments/{id}")
  Payment getPayment(@PathVariable Long id) {

    return paymentService.findById(id)
        .orElseThrow(() -> new PaymentNotFoundException(id));
  }
}