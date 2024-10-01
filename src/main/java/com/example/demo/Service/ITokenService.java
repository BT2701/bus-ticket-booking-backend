package com.example.demo.Service;

import com.example.demo.Model.Customer;
import com.example.demo.Model.Token;

public interface ITokenService {
    Token addToken(Customer customer, String token);
    Token refreshToken(String refreshToken, Customer userDetails) throws Exception;
}

