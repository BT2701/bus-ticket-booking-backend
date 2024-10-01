package com.example.demo.Controller;

import com.example.demo.DTO.*;
import com.example.demo.Model.Customer;
import com.example.demo.Model.Token;
import com.example.demo.Service.CustomerService;
import com.example.demo.Service.TokenSV;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomersCTL {
    private final CustomerService customerService;
    private final TokenSV tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register (@Valid @RequestBody CustomerDTO customerDTO, BindingResult result) throws Exception {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                return ResponseEntity.badRequest().body(errorMessages);
            }


            if(!customerDTO.getPassword().equals(customerDTO.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Password does not match !");
            }

            Customer customer = customerService.createCustomer(customerDTO);

            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error while registering user: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@Valid @RequestBody LoginDTO loginDTO, BindingResult result) throws Exception {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                       .stream()
                       .map(FieldError::getDefaultMessage)
                       .toList();

                return ResponseEntity.badRequest().body(errorMessages);
            }

            String token = customerService.login(loginDTO.getEmailOrPhone(), loginDTO.getPassword());
            Customer customerDetails = customerService.getCustomerDetailsFromToken(token);
            Token jwtToken = tokenService.addToken(customerDetails, token);

            return ResponseEntity.ok(LoginResponseDTO.builder()
                    .message("Login successfully !")
                    .id(customerDetails.getId())
                    .username(customerDetails.getPhone())
                    .accessToken(jwtToken.getAccessToken())
                    .refreshToken(jwtToken.getRefreshToken())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error while logging in: " + e.getMessage());
        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO, BindingResult result) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages =  result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                return ResponseEntity.badRequest().body(errorMessages);
            }

            if(!forgotPasswordDTO.getPassword().equals(forgotPasswordDTO.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Password and retype password do not match !");
            }

            String resetToken = forgotPasswordDTO.getResetToken();
            Customer userDetails = customerService.getCustomerDetailsFromToken(resetToken);
            String rs = customerService.changePassword(resetToken, userDetails, forgotPasswordDTO.getPassword());

            return ResponseEntity.ok(rs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<LoginResponseDTO> refreshToken(@Valid @RequestBody Map<String, String> refreshToken) {
        try {
            Customer userDetails = customerService.getCustomerDetailsFromRefreshToken(refreshToken.get("refreshToken"));
            Token jwtToken = tokenService.refreshToken(refreshToken.get("refreshToken"), userDetails);

            return ResponseEntity.ok(LoginResponseDTO.builder()
                    .message("Refresh token successfully !")
                    .accessToken(jwtToken.getAccessToken())
                    .refreshToken(jwtToken.getRefreshToken())
                    .id(userDetails.getId())
                    .username(userDetails.getUsername())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponseDTO.builder()
                            .message("Refresh token failed !")
                            .build()
            );
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String token) {
        try {
            String extractedToken = token.substring(7); // loại bỏ Bearer
            Customer user = customerService.getCustomerDetailsFromToken(extractedToken);

            CustomerResponseDTO customerResponseDTO = CustomerResponseDTO.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .address(user.getAddress())
                    .phone(user.getPhone())
                    .birth(user.getBirth())
                    .bookings(user.getBookings())
                    .tokens(user.getTokens())
                    .build();
            return ResponseEntity.ok(customerResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody CustomerDTO customerUpdateDTO,
            BindingResult result,
            @RequestHeader("Authorization") String authorization
    ) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            String extractedToken = authorization.substring(7);
            Customer user = customerService.getCustomerDetailsFromToken(extractedToken);
            if(user.getId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Customer updatedUser = customerService.updateCustomer(userId, customerUpdateDTO);

            CustomerResponseDTO customerResponseDTO = CustomerResponseDTO.builder()
                    .id(updatedUser.getId())
                    .name(updatedUser.getName())
                    .email(updatedUser.getEmail())
                    .address(updatedUser.getAddress())
                    .phone(updatedUser.getPhone())
                    .birth(updatedUser.getBirth())
                    .bookings(updatedUser.getBookings())
                    .tokens(updatedUser.getTokens())
                    .build();
            return ResponseEntity.ok(customerResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
