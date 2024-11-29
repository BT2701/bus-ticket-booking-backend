package com.example.demo.Controller;

import com.example.demo.DTO.*;
import com.example.demo.Model.Customer;
import com.example.demo.Model.Token;
import com.example.demo.Service.CustomerService;
import com.example.demo.Service.TokenSV;
import com.example.demo.exceptions.InvalidTokenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    private final HttpServletRequest request;

    @GetMapping("/oauth2-infor")
    public ResponseEntity<?> getUserAfterLoginWithOauth2(
            @AuthenticationPrincipal OAuth2User principal
    ) {
        try {
            if (principal == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                return ResponseEntity.ok(null);
            }

            Map<String, Object> attributes = principal.getAttributes();
            String email = attributes.get("email") == null ? attributes.get("login").toString() + "@gmail.com" : attributes.get("email").toString();
            Customer customer = customerService.getCustomerByEmail(email);

            // lần đầu đăng nhập bằng oauth2
            if(customer.getPhone() == null || customer.getPhone().isEmpty()) {
                return ResponseEntity.ok(
                        ResponseDTO.builder()
                                .status(HttpStatus.OK.value())
                                .message("Đăng nhập thành công !")
                                .data(customer)
                                .build()
                );
            }

            if(!customer.isActive()) {
                return ResponseEntity.ok(
                        ResponseDTO.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Tài khoản của bạn đã bị khóa !")
                                .data(customer)
                                .build()
                );
            }

            String token = customerService.loginByOauth2(customer.getEmail());
            Customer customerDetails = customerService.getCustomerDetailsFromToken(token);
            Token jwtToken = tokenService.addToken(customerDetails, token);

            Oauth2ResponseCustomerDTO oauth2ResponseCustomerDTO = Oauth2ResponseCustomerDTO.builder()
                    .email(customer.getEmail())
                    .name(customer.getName())
                    .phone(customer.getPhone())
                    .birth(customer.getBirth())
                    .id(customer.getId())
                    .address(customer.getAddress())
                    .accessToken(jwtToken.getAccessToken())
                    .refreshToken(jwtToken.getRefreshToken())
                    .build();

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.OK.value())
                            .message("Đăng nhập thành công !")
                            .data(oauth2ResponseCustomerDTO)
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }
    @PostMapping("/oauth2-create-password")
    public ResponseEntity<?> createPasswordForOauth2(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreatePasswordDTO createPasswordDTO,
            BindingResult result
    ) {
        try {
            ResponseEntity<ResponseDTO> errorResponse = CustomersCTL.handleValidationErrors(result);
            if (errorResponse != null) {
                return errorResponse;
            }

            String accessToken = token.substring(7);
            Customer customer = customerService.getCustomerDetailsFromToken(accessToken);

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.OK.value())
                            .message(customerService.updateCustomerPasswordForOauth2(customer.getEmail(), createPasswordDTO.getPassword()))
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @PostMapping("/oauth2-logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            // remove session
            request.getSession().invalidate();

            // remove cookie
            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            return ResponseEntity.ok("Đăng xuất thành công!");
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @PutMapping("/oauth2-update-infor-on-first-login")
    public ResponseEntity<?> updateUserOnFirstLoginWithOauth2(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestBody UpdateCustomerDTO updateCustomerDTO
    ) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        ResponseDTO.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .message("Lỗi đăng nhập bên thứ 3!")
                                .build()
                );
            }

            Map<String, Object> attributes = principal.getAttributes();
            String email = attributes.get("email") == null ? attributes.get("login").toString() + "@gmail.com" : attributes.get("email").toString();

            // Kiểm tra email trong OAuth2User có trùng với email từ client hay không
            if (!email.equals(updateCustomerDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ResponseDTO.builder()
                                .status(HttpStatus.FORBIDDEN.value())
                                .message("Email không khớp, không thể cập nhật!")
                                .build()
                );
            }

            Customer existCustomer = customerService.getCustomerByEmail(updateCustomerDTO.getEmail());

            if (existCustomer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseDTO.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .message("Không tìm thấy người dùng với email : " + email +" !")
                                .build()
                );
            }

            Customer customer = customerService.updateCustomer(existCustomer.getId(), updateCustomerDTO);
            String token = customerService.loginByOauth2(updateCustomerDTO.getEmail());
            Customer customerDetails = customerService.getCustomerDetailsFromToken(token);
            Token jwtToken = tokenService.addToken(customerDetails, token);

            Oauth2ResponseCustomerDTO oauth2ResponseCustomerDTO = Oauth2ResponseCustomerDTO.builder()
                    .email(customer.getEmail())
                    .name(customer.getName())
                    .phone(customer.getPhone())
                    .birth(customer.getBirth())
                    .id(customer.getId())
                    .address(customer.getAddress())
                    .accessToken(jwtToken.getAccessToken())
                    .refreshToken(jwtToken.getRefreshToken())
                    .build();

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.OK.value())
                            .message("Cập nhật thông tin thành công!")
                            .data(oauth2ResponseCustomerDTO)
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @GetMapping("")
    public Page<Customer> getCustomers(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        return customerService.getCustomers(pageNo, pageSize, sortBy, sortDir);
    }

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
                            .message("Đăng ký thành công! Hãy xác thực tài khoản tại email của bạn!")
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

    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam String verifyToken) {
        try {
            return ResponseEntity.ok(customerService.verifyCustomer(verifyToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Xác thực thất bại: " + e.getMessage());
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
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token, HttpServletResponse response) {
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

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.ACCEPTED.value())
                            .message("Lấy thông tin chi tiết người dùng thành công!")
                            .data(user)
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

    @PutMapping("/updateUserFromAdmin/{userId}")
    public ResponseEntity<?> updateUserByAdmin(
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
            if (!customerService.isAdminFromToken(extractedToken)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ResponseDTO.builder()
                                .status(HttpStatus.FORBIDDEN.value())
                                .message("Bạn không có quyền truy cập tài nguyên này!")
                                .data(null)
                                .build()
                );
            }

            Customer updatedUser = customerService.updateCustomer(userId, customerUpdateDTO);

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(HttpStatus.ACCEPTED.value())
                            .message("Cập nhật người dùng thành công!")
                            .data(updatedUser)
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
        if (e instanceof InvalidTokenException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseDTO.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }

        return ResponseEntity.badRequest().body(
                ResponseDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(e.getMessage())
                        .data(null)
                        .build()
        );
    }
}
