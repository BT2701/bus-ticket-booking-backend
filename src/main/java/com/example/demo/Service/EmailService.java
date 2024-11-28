package com.example.demo.Service;

import com.example.demo.Model.Customer;
import com.example.demo.Model.Token;
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
    private final TokenSV tokenSV;

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
        Optional<Customer> customer = customerRepo.findCustomerByEmail(to);
        if(customer.isEmpty()) {
            throw new MessagingException("Không tìm thấy người dùng với email này!");
        } else if(customer.get().getPassword() == null || customer.get().getPassword().isEmpty()) {
            throw new MessagingException(String.format("Không thể quên mật khẩu vì đây là tài khoản đăng nhập từ %s !", customer.get().getProvider()));
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setFrom(emailFrom);
        helper.setTo(to);
        helper.setSubject("QUÊN MẬT KHẨU");

        String resetToken = forgotpasswordSV.createForgotPassword(customer.get());

        Context context = new Context();
        String linkConfirm = String.format("http://localhost:3000/reset-password?resetToken=%s", resetToken);
        Map<String, Object> properties = new HashMap<>();
        properties.put("linkConfirm", linkConfirm);
        context.setVariables(properties);

        String html = templateEngine.process("confirm-email.html", context);
        helper.setText(html, true);

        mailSender.send(message);
        return "Email đã được gửi thành công!";
    }

    public String sendEmailToVerify(Customer customer, String verifyToken) throws Exception {
        Token jwtToken = tokenSV.addTokenToVerify(customer, verifyToken);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setFrom(emailFrom);
        helper.setTo(customer.getEmail());
        helper.setSubject("XÁC THỰC TÀI KHOẢN");

        Context context = new Context();
        String linkConfirm = String.format("http://localhost:3000/login?verifyToken=%s", jwtToken.getAccessToken());
        Map<String, Object> properties = new HashMap<>();
        properties.put("linkConfirm", linkConfirm);
        context.setVariables(properties);

        String html = templateEngine.process("verify-email.html", context);
        helper.setText(html, true);

        mailSender.send(message);
        return "Tài khoản chưa xác thực ! Tin nhắn xác thực vừa được gửi tới email của bạn!";
    }
}
