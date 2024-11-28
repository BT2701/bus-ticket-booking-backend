package com.example.demo.Controller;

import com.example.demo.DTO.PaymentDTO;
import com.example.demo.Service.PaymentSV;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PaymentCTL {
    @Autowired
    private PaymentSV paymentSV;

    @PostMapping("/payment")
    public void addPayment(@Valid @RequestBody PaymentDTO payment) {
        paymentSV.addPayment(payment);
    }
}
