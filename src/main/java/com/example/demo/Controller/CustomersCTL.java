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

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.CREATED.value())
                            .message("Register successfully!")
                            .data(customer)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
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

                return ResponseEntity.ok(
                        ResponseDTO.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(String.valueOf(errorMessages))
                                .data(null)
                                .build()
                );
            }

            String token = customerService.login(loginDTO.getEmailOrPhone(), loginDTO.getPassword());
            Customer customerDetails = customerService.getCustomerDetailsFromToken(token);
            Token jwtToken = tokenService.addToken(customerDetails, token);

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.ACCEPTED.value())
                            .message("Login successfully !")
                            .data(LoginResponseDTO.builder()
                                    .id(customerDetails.getId())
                                    .username(customerDetails.getPhone())
                                    .accessToken(jwtToken.getAccessToken())
                                    .refreshToken(jwtToken.getRefreshToken())
                                    .build()
                            )
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    @PutMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO, BindingResult result) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages =  result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                return ResponseEntity.ok(
                        ResponseDTO.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(String.valueOf(errorMessages))
                                .data(null)
                                .build()
                );
            }

            if(!forgotPasswordDTO.getPassword().equals(forgotPasswordDTO.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Password and retype password do not match !");
            }

            String resetToken = forgotPasswordDTO.getResetToken();
            Customer userDetails = customerService.getCustomerDetailsFromToken(resetToken);
            String rs = customerService.changeAfterForgotPassword(resetToken, userDetails, forgotPasswordDTO.getPassword());

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.NO_CONTENT.value())
                            .message(rs)
                            .data(null)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody Map<String, String> refreshToken) {
        try {
            Customer userDetails = customerService.getCustomerDetailsFromRefreshToken(refreshToken.get("refreshToken"));
            Token jwtToken = tokenService.refreshToken(refreshToken.get("refreshToken"), userDetails);

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.ACCEPTED.value())
                            .message("Refresh token successfully !")
                            .data(LoginResponseDTO.builder()
                                    .accessToken(jwtToken.getAccessToken())
                                    .refreshToken(jwtToken.getRefreshToken())
                                    .id(userDetails.getId())
                                    .username(userDetails.getUsername())
                                    .build())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                        ResponseDTO.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Refresh token failed !")
                            .data(null)
                            .build()
            );
        }
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String token, @Valid @RequestBody UpdatePasswordDTO updatePasswordDTO, BindingResult result) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages =  result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                return ResponseEntity.ok(
                        ResponseDTO.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(String.valueOf(errorMessages))
                                .data(null)
                                .build()
                );
            }

            String accessToken = token.substring(7);

            if (!updatePasswordDTO.getNewPassword().equals(updatePasswordDTO.getConfirmNewPassword())) {
                return ResponseEntity.ok(
                        ResponseDTO.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("New password and confirm password do not match!")
                                .data(null)
                                .build()
                );
            }

            customerService.updatePassword(accessToken, updatePasswordDTO.getOldPassword(), updatePasswordDTO.getNewPassword());
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.NO_CONTENT.value())
                            .message("Password updated successfully!")
                            .data(null)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            String accessToken = token.substring(7);
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.NO_CONTENT.value())
                            .message(customerService.logout(accessToken))
                            .data(null)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .data(null)
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
                    .build();

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.ACCEPTED.value())
                            .message("Get details of the customer successfully!")
                            .data(customerResponseDTO)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody UpdateCustomerDTO customerUpdateDTO,
            BindingResult result,
            @RequestHeader("Authorization") String authorization
    ) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.ok(
                        ResponseDTO.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(String.valueOf(errorMessages))
                                .data(null)
                                .build()
                );
            }
            String extractedToken = authorization.substring(7);
            Customer user = customerService.getCustomerDetailsFromToken(extractedToken);
            if(user.getId() != userId) {
                return ResponseEntity.ok(
                        ResponseDTO.builder()
                                .status(HttpStatus.FORBIDDEN.value())
                                .message("Invalid user!")
                                .data(null)
                                .build()
                );
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
                    .build();

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.ACCEPTED.value())
                            .message("Update user successfully!")
                            .data(customerResponseDTO)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }
}
