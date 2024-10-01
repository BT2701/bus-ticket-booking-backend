package com.example.demo.Service;

import com.example.demo.Model.Customer;
import com.example.demo.Model.ForgotPassword;
import com.example.demo.Model.Token;
import com.example.demo.Repository.ForgotPasswordRepo;
import com.example.demo.Utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ForgotpasswordSV implements IForgotPasswordSV{
    private final JwtUtils jwtUtils;
    private final ForgotPasswordRepo forgotPasswordRepo;

    @Value("${application.security.jwt.reset-token-validity}")
    private Long resetTokenExpire;

    @Override
    public String createForgotPassword(Customer customer) throws Exception {
        String resetToken = jwtUtils.generateResetToken(customer);

        ForgotPassword forgotPassword = ForgotPassword.builder()
                .customer(customer)
                .resetToken(resetToken)
                .resetExpirationDate(LocalDateTime.now().plusSeconds(resetTokenExpire))
                .build();

        forgotPasswordRepo.save(forgotPassword);

        return forgotPassword.getResetToken();
    }
}
