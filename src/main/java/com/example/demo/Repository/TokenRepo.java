package com.example.demo.Repository;

import com.example.demo.Model.Customer;
import com.example.demo.Model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepo extends JpaRepository<Token, Long> {
    Token findByToken(String token);

    Token findByRefreshToken(String refreshToken);

    List<Token> findByCustomer(Customer customer);
}
