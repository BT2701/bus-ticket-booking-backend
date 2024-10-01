package com.example.demo.Service;

import com.example.demo.Model.Customer;

public interface IForgotPasswordSV {
    String createForgotPassword(Customer customer) throws Exception;
}
