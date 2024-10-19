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
            ResponseEntity<ResponseDTO> errorResponse = CustomersCTL.handleValidationErrors(result);
            if (errorResponse != null) {
                return errorResponse;
            }


            if(!customerDTO.getPassword().equals(customerDTO.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(
                        ResponseDTO.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Mật khẩu và xác nhận mật khẩu không trùng nhau !")
                                .data(null)
                                .build()
                );
            }

            Customer customer = customerService.createCustomer(customerDTO);

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.CREATED.value())
                            .message("Đăng ký thành công!")
                            .data(customer)
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@Valid @RequestBody LoginDTO loginDTO, BindingResult result) throws Exception {
        try {
            ResponseEntity<ResponseDTO> errorResponse = CustomersCTL.handleValidationErrors(result);
            if (errorResponse != null) {
                return errorResponse;
            }

            String token = customerService.login(loginDTO.getEmailOrPhone(), loginDTO.getPassword());
            Customer customerDetails = customerService.getCustomerDetailsFromToken(token);
            Token jwtToken = tokenService.addToken(customerDetails, token);

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.ACCEPTED.value())
                            .message("Đăng nhập thành công !")
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
            return CustomersCTL.handleError(e);
        }
    }

    @PutMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO, BindingResult result) {
        try {
            ResponseEntity<ResponseDTO> errorResponse = CustomersCTL.handleValidationErrors(result);
            if (errorResponse != null) {
                return errorResponse;
            }

            if(!forgotPasswordDTO.getPassword().equals(forgotPasswordDTO.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(
                        ResponseDTO.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Mật khẩu và xác nhận mật khẩu không trùng nhau !")
                                .data(null)
                                .build()
                );
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
            return CustomersCTL.handleError(e);
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
                            .message("Làm mới token thành công !")
                            .data(LoginResponseDTO.builder()
                                    .accessToken(jwtToken.getAccessToken())
                                    .refreshToken(jwtToken.getRefreshToken())
                                    .id(userDetails.getId())
                                    .username(userDetails.getUsername())
                                    .build())
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String token, @Valid @RequestBody UpdatePasswordDTO updatePasswordDTO, BindingResult result) {
        try {
            ResponseEntity<ResponseDTO> errorResponse = CustomersCTL.handleValidationErrors(result);
            if (errorResponse != null) {
                return errorResponse;
            }

            String accessToken = token.substring(7);

            if (!updatePasswordDTO.getNewPassword().equals(updatePasswordDTO.getConfirmNewPassword())) {
                return ResponseEntity.badRequest().body(
                        ResponseDTO.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Mật khẩu mới và xác nhận mật khẩu mới không trùng nhau !")
                                .data(null)
                                .build()
                );
            }

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.NO_CONTENT.value())
                            .message(customerService.updatePassword(accessToken, updatePasswordDTO.getOldPassword(), updatePasswordDTO.getNewPassword()))
                            .data(null)
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
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
            return CustomersCTL.handleError(e);
        }
    }

    @PostMapping("/logoutAll")
    public ResponseEntity<?> logoutAll(@RequestHeader("Authorization") String token) {
        try {
            String accessToken = token.substring(7);
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.NO_CONTENT.value())
                            .message(customerService.logoutAllFromAccessToken(accessToken))
                            .data(null)
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
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
                            .message("Lấy thông tin chi tiết người dùng thành công!")
                            .data(customerResponseDTO)
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
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
            ResponseEntity<ResponseDTO> errorResponse = CustomersCTL.handleValidationErrors(result);
            if (errorResponse != null) {
                return errorResponse;
            }

            String extractedToken = authorization.substring(7);
            Customer user = customerService.getCustomerDetailsFromToken(extractedToken);
            if(user.getId() != userId) {
                return ResponseEntity.badRequest().body(
                        ResponseDTO.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .message("Người dùng không trùng khớp với id = " + userId)
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
                            .message("Cập nhật người dùng thành công!")
                            .data(customerResponseDTO)
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @PutMapping("/lock/{id}")
    public ResponseEntity<?> lockAccount(@PathVariable int id) {
        try {
            boolean isLocked = customerService.lockAccount(id);

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.ACCEPTED.value())
                            .message("Tài khoản đã bị khóa.")
                            .data(isLocked)
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @PutMapping("/unlock/{id}")
    public ResponseEntity<?> unlockAccount(@PathVariable int id) {
        try {
            boolean isLocked = customerService.unlockAccount(id);

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.ACCEPTED.value())
                            .message("Tài khoản đã được mở khóa.")
                            .data(isLocked)
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    public static ResponseEntity<ResponseDTO> handleValidationErrors(BindingResult result) {
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            return ResponseEntity.badRequest().body(
                    ResponseDTO.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(String.join(", ", errorMessages))
                            .data(null)
                            .build()
            );
        }
        return null;
    }

    public static ResponseEntity<?> handleError(Exception e) {
        return ResponseEntity.badRequest().body(
                ResponseDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(e.getMessage())
                        .data(null)
                        .build()
        );
    }
}
