package com.example.demo.Controller;

import com.example.demo.DTO.EmailDTO;
import com.example.demo.Service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/mail")
@RequiredArgsConstructor
public class EmailCTL {
    private final EmailService emailService;

    @GetMapping("/sendEmailToForgotPassword")
    public ResponseEntity<?> sendEmail(@RequestParam String email) {
        try {
            return ResponseEntity.accepted().body((emailService.sendEmailToForgotPassword(email)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Sending email was failure : " + e.getMessage());
        }
    }
}
