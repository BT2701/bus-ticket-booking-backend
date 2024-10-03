package com.example.demo.Service;

import com.example.demo.DTO.CustomerDTO;
import com.example.demo.DTO.ForgotPasswordDTO;
import com.example.demo.DTO.UpdateCustomerDTO;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
                .birth(customerDTO.getBirth())
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
        } catch (BadCredentialsException e) {
            throw new Exception ("Could not login with error : Incorrect password");
        } catch (Exception e) {
            throw new Exception("Could not login with error : Invalid username or password AND the error is " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public String changeAfterForgotPassword(String resetToken, Customer customer, String newPassword) throws Exception {
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
        try {
            if(jwtUtils.isTokenExpired(token)) {
                throw new DateTimeException("Token is expired !");
            }

            String phoneNumber = jwtUtils.extractPhoneNumber(token);

            return customerRepo
                    .findCustomerByPhone(phoneNumber)
                    .orElseThrow(() -> new Exception("Could not find user with phone number " + phoneNumber));
        } catch (Exception e) {
            throw new Exception("Token does not exist or does not match current user");
        }
    }

    @Override
    public Customer getCustomerDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token token = tokenRepo.findByRefreshToken(refreshToken);

        return getCustomerDetailsFromToken(token.getAccessToken());
    }

    @Override
    public Customer updateCustomer(Integer customerId, UpdateCustomerDTO customerUpadtedDTO) throws Exception {
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

        return customerRepo.save(existingCustomer);
    }


    @Override
    public String updatePassword(String accessToken, String oldPassword, String newPassword) throws Exception {
        try {
            Customer existingCustomer = getCustomerDetailsFromToken(accessToken);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    existingCustomer.getPhone(), oldPassword, existingCustomer.getAuthorities()
            );

            Authentication auth = authenticationManager.authenticate(authenticationToken);
            if(auth.isAuthenticated()) {
                String encodeNewPassword = passwordEncoder.encode(newPassword);
                existingCustomer.setPassword(encodeNewPassword);
            }

            customerRepo.save(existingCustomer);

            return "Password was successfully changed!";
        } catch (BadCredentialsException e) {
            return "Incorrect old password";
        } catch (Exception e) {
            throw new Exception("Could not change password with error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public String logout(String accessToken) throws Exception {
        try {
            Customer customer = getCustomerDetailsFromToken(accessToken);
            tokenRepo.deleteTokenByAccessTokenAndCustomer(accessToken, customer);

            return "User logged out successfully!";
        } catch (Exception e) {
            throw new Exception("Could not logout with error: " + e.getMessage());
        }
    }
}
