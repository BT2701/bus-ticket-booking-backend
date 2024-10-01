package com.example.demo.Service;

import com.example.demo.DTO.CustomerDTO;
import com.example.demo.DTO.ForgotPasswordDTO;
import com.example.demo.Model.Customer;
import com.example.demo.Model.ForgotPassword;
import com.example.demo.Model.Token;
import com.example.demo.Repository.CustomerRepo;
import com.example.demo.Repository.ForgotPasswordRepo;
import com.example.demo.Repository.TokenRepo;
import com.example.demo.Utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService{
    private final CustomerRepo customerRepo;
    private final JwtUtils jwtUtils;

    private final TokenRepo tokenRepo;
    private final ForgotPasswordRepo forgotPasswordRepo;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    @Override
    public Customer createCustomer(CustomerDTO customerDTO) throws Exception {
        String phoneNumber = customerDTO.getPhone();
        String email = customerDTO.getEmail();

        if(customerRepo.existsByEmail(email)) {
            throw new Exception("Email already exists !");
        }

        if(customerRepo.existsByPhone(phoneNumber)) {
            throw new Exception("Phone number already exists !");
        }

        Customer newCustomer = Customer.builder()
                .address(customerDTO.getAddress())
                .email(customerDTO.getEmail())
                .phone(customerDTO.getPhone())
                .name(customerDTO.getName())
                .password(customerDTO.getPassword())
                .tokens(null)
                .bookings(null)
                .forgotPassword(null)
                .build();

        String encodePassword = passwordEncoder.encode(customerDTO.getPassword());
        newCustomer.setPassword(encodePassword);

        return customerRepo.save(newCustomer);
    }

    @Override
    public String login(String emailOrPhone, String password) throws Exception {
        try {
            Optional<Customer> customer;

            if (emailOrPhone.contains("@")) {
                customer = customerRepo.findCustomerByEmail(emailOrPhone);
            } else {
                customer = customerRepo.findCustomerByPhone(emailOrPhone);
            }
            if(customer.isEmpty()) {
                throw new Exception("Invalid phonenumber or email !");
            }

            Customer existingCustomer = customer.get();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    existingCustomer.getPhone(), password, existingCustomer.getAuthorities()
            );
            authenticationManager.authenticate(authenticationToken);

            return jwtUtils.generateToken(existingCustomer);
        } catch (Exception e) {
            throw new Exception("Invalid username or password AND the error is " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public String changePassword(String resetToken, Customer customer, String newPassword) throws Exception {
        try {
            if(newPassword != null && !newPassword.isEmpty()) {
                String encodeNewPassword = passwordEncoder.encode(newPassword);
                customer.setPassword(encodeNewPassword);
            }

            ForgotPassword forgotPassword = forgotPasswordRepo.findByResetTokenAndCustomer(resetToken, customer);
            forgotPasswordRepo.delete(forgotPassword);

            tokenRepo.deleteAllTokensByCustomer(customer);
            customerRepo.save(customer);
            return "Password was successfully changed!";
        } catch (Exception e) {
            throw new Exception("Could not change password with error : " + e.getMessage());
        }
    }

    @Override
    public Customer getCustomerDetailsFromToken(String token) throws Exception {
        if(jwtUtils.isTokenExpired(token)) {
            throw new DateTimeException("Token is expired !");
        }

        String phoneNumber = jwtUtils.extractPhoneNumber(token);

        return customerRepo
                .findCustomerByPhone(phoneNumber)
                .orElseThrow(() -> new Exception("Could not find user with phone number " + phoneNumber));
    }

    @Override
    public Customer getCustomerDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token token = tokenRepo.findByRefreshToken(refreshToken);

        return getCustomerDetailsFromToken(token.getAccessToken());
    }

    @Override
    public Customer updateCustomer(Integer customerId, CustomerDTO customerUpadtedDTO) throws Exception {
        Customer existingCustomer = customerRepo.findById(customerId)
                .orElseThrow(() -> new Exception("Could not find user with id " + customerId));

        String phoneNumber = customerUpadtedDTO.getPhone();
        if(!existingCustomer.getPhone().equals(phoneNumber) && customerRepo.existsByPhone(phoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists !");
        }

        if (customerUpadtedDTO.getName() != null) {
            existingCustomer.setName(customerUpadtedDTO.getName());
        }
        if (customerUpadtedDTO.getPhone() != null) {
            existingCustomer.setPhone(customerUpadtedDTO.getPhone());
        }
        if (customerUpadtedDTO.getAddress() != null) {
            existingCustomer.setAddress(customerUpadtedDTO.getAddress());
        }
        if (customerUpadtedDTO.getBirth() != null) {
            existingCustomer.setBirth(customerUpadtedDTO.getBirth());
        }
        if (customerUpadtedDTO.getEmail() != null) {
            existingCustomer.setEmail(customerUpadtedDTO.getEmail());
        }

        // encode password before updating
        if(customerUpadtedDTO.getPassword() != null && !customerUpadtedDTO.getPassword().isEmpty()) {
            if(customerUpadtedDTO.getConfirmPassword() == null || !customerUpadtedDTO.getPassword().equals(customerUpadtedDTO.getConfirmPassword())) {
                throw new Exception("Password and retype password not the same !");
            }

            String newPassword = customerUpadtedDTO.getPassword();
            String encodeNewPassword = passwordEncoder.encode(newPassword);
            existingCustomer.setPassword(encodeNewPassword);
        }

        return customerRepo.save(existingCustomer);
    }
}
