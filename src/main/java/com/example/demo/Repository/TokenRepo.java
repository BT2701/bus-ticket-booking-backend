package com.example.demo.Repository;

import com.example.demo.Model.Customer;
import com.example.demo.Model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {
    Token findByAccessToken(String accessToken);

    Token findByRefreshToken(String refreshToken);

    List<Token> findByCustomer(Customer customer);

    void deleteAllTokensByCustomer(Customer customer);
    void deleteTokenByAccessTokenAndCustomer(String accessToken, Customer customer);
}
