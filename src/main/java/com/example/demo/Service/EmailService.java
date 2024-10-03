package com.example.demo.Service;

import com.example.demo.Model.Customer;
import com.example.demo.Repository.CustomerRepo;
import com.example.demo.Repository.TokenRepo;
import com.example.demo.Utils.JwtUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final CustomerRepo customerRepo;
    private final ForgotpasswordSV forgotpasswordSV;

    @Value("${spring.mail.from}")
    private String emailFrom;

    public String sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        return "Email sent successfully";
    }
    public String sendEmailToForgotPassword(String to) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setFrom(emailFrom);
        helper.setTo(to);
        helper.setSubject("CONFIRM FORGOT PASSWORD");

        Optional<Customer> customer = customerRepo.findCustomerByEmail(to);
        if(customer.isEmpty()) {
            throw new MessagingException("User with the email was not found!");
        }
        String resetToken = forgotpasswordSV.createForgotPassword(customer.get());

        Context context = new Context();
        String linkConfirm = String.format("http://localhost:3000/reset-password?resetToken=%s", resetToken);
        Map<String, Object> properties = new HashMap<>();
        properties.put("linkConfirm", linkConfirm);
        context.setVariables(properties);

        String html = templateEngine.process("confirm-email.html", context);
        helper.setText(html, true);

        mailSender.send(message);
        return "Email sent successfully";
    }
}
