package com.example.demo.Service;

import com.example.demo.DTO.CustomerDTO;
import com.example.demo.Model.Customer;

public interface ICustomerService {
    Customer createCustomer(CustomerDTO customerDTO) throws Exception;
    String login (String emailOrPhone, String password) throws Exception;

    Customer getCustomerDetailsFromToken(String token) throws Exception;
    Customer getCustomerDetailsFromRefreshToken(String refreshToken) throws Exception;

    Customer updateCustomer(Integer customerId, CustomerDTO customerUpadtedDTO) throws Exception;
}
