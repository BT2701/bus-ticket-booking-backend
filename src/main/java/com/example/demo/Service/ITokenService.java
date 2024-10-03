package com.example.demo.Service;

import com.example.demo.Model.Customer;
import com.example.demo.Model.Token;

import java.util.List;

public interface ITokenService {
    List<Token> getAllTokensByCustomer(Customer customer);
    void deleteAllTokensByCustomer(Customer customer);
    Token addToken(Customer customer, String token);
    Token refreshToken(String refreshToken, Customer userDetails) throws Exception;
}

