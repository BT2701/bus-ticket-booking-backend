package com.example.demo.Repository;

import com.example.demo.Model.Customer;
import com.example.demo.Model.ForgotPassword;
import com.example.demo.Model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForgotPasswordRepo extends JpaRepository<ForgotPassword, Integer> {
    ForgotPassword findByResetTokenAndCustomer(String resetToken, Customer customer);
}
