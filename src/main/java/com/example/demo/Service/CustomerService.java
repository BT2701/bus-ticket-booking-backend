package com.example.demo.Service;

import com.example.demo.DTO.CustomerDTO;
import com.example.demo.DTO.ResponseDTO;
import com.example.demo.DTO.UpdateCustomerDTO;
import com.example.demo.Model.Customer;
import com.example.demo.Model.ForgotPassword;
import com.example.demo.Model.Role;
import com.example.demo.Model.Token;
import com.example.demo.Repository.*;
import com.example.demo.Utils.JwtUtils;
import com.example.demo.exceptions.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService{
    private final CustomerRepo customerRepo;
    private final JwtUtils jwtUtils;

    private final TokenRepo tokenRepo;
    private final EmailService emailService;
    private final RoleRepo roleRepo;
    private final ForgotPasswordRepo forgotPasswordRepo;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public Page<Customer> getCustomers(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return customerRepo.findAll(pageable);
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepo.findCustomerByEmail(email).orElse(null);
    }
    public Customer getCustomerByPhone(String phone) {
        return customerRepo.findCustomerByPhone(phone).orElse(null);
    }

    @Override
    public Customer createCustomer(CustomerDTO customerDTO) throws Exception {
        String phoneNumber = customerDTO.getPhone();
        String email = customerDTO.getEmail();

        if(customerRepo.existsByEmail(email)) {
            throw new Exception("Email đã tồn tại !");
        }

        if(customerRepo.existsByPhone(phoneNumber)) {
            throw new Exception("Phone đã tồn tại !");
        }

        Role role = roleRepo.findByName(Role.ROLE_CUSTOMER);

        Customer newCustomer = Customer.builder()
                .address(customerDTO.getAddress())
                .email(customerDTO.getEmail())
                .phone(customerDTO.getPhone())
                .name(customerDTO.getName())
                .password(customerDTO.getPassword())
                .birth(customerDTO.getBirth())
                .tokens(null)
                .bookings(null)
                .role(role)
                .forgotPassword(null)
                .isActive(true)
                .isVerified(false)
                .provider(ProviderType.DATABASE)
                .build();

        String encodePassword = passwordEncoder.encode(customerDTO.getPassword());
        newCustomer.setPassword(encodePassword);

        customerRepo.save(newCustomer);
        String verifyToken = jwtUtils.generateToken(newCustomer);
        emailService.sendEmailToVerify(newCustomer, verifyToken);

        return newCustomer;
    }

    @Transactional
    public String verifyCustomer (String token) throws Exception {
        try {
            Token tk = tokenRepo.findByAccessToken(token);
            if(tk == null) {
                throw new Exception("Mã xác thực không tồn tại!");
            }
            if(jwtUtils.isTokenExpired(token)) {
                tokenRepo.deleteTokenByAccessToken(token);
                throw new DateTimeException("Mã xác thực đã hết hạn ! Bấm đăng nhập để được gửi lại mã mới !");
            }

            Customer customer = getCustomerDetailsFromToken(token);
            customer.setVerified(true);
            customerRepo.save(customer);
            tokenRepo.deleteTokenByAccessToken(token);
            return "Tài khoản đã được xác thực thành công !";
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
    public String updateCustomerPasswordForOauth2(String email, String newPassword) throws Exception {
        try {
            Optional<Customer> existingCustomer = customerRepo.findCustomerByEmail(email);

            if(existingCustomer.isEmpty()) {
                throw new Exception("Tài khoản không tồn tại!");
            }
            if (existingCustomer.get().getPassword() != null && !existingCustomer.get().getPassword().isEmpty()) {
                throw new Exception("Tài khoản này đã có mật khẩu!");
            }

            String encodeNewPassword = passwordEncoder.encode(newPassword);
            existingCustomer.get().setPassword(encodeNewPassword);

            customerRepo.save(existingCustomer.get());
            return "Mật khẩu đã được thêm thành công !";
        } catch (BadCredentialsException e) {
            throw new Exception("Mật khẩu cũ không chính xác !");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Không thể thay đổi mật khẩu !");
        }
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
                throw new Exception("Tài khoản không tồn tại !");
            }
            if(!customer.get().isActive()) {
                throw new Exception("Tài khoản đã bị khóa !");
            }
            if(!customer.get().isVerified()) {
                List<Token> tokens = tokenRepo.findByCustomer(customer.get());
                if(tokens.isEmpty()) {
                    throw new Exception("Mã xác thực đã được gửi qua email của bạn !");
                }

                String verifyToken = jwtUtils.generateToken(customer.get());
                String sendEmail = emailService.sendEmailToVerify(customer.get(), verifyToken);

                throw new Exception(sendEmail);
            }

            Customer existingCustomer = customer.get();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    existingCustomer.getPhone(), password, existingCustomer.getAuthorities()
            );
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return jwtUtils.generateToken(existingCustomer);
        } catch (BadCredentialsException e) {
            throw new Exception ("Mật khẩu không chính xác !");
        } catch (Exception e) {
            System.out.println(e.getMessage());
//            throw new Exception("Tài khoản hoặc mật khẩu không chính xác " + e.getMessage());
            throw new Exception("Không thể đăng nhập vì : " + e.getMessage());
        }
    }

    public String loginByOauth2(String email) throws Exception {
        try {
            Optional<Customer> customer = customerRepo.findCustomerByEmail(email);

            if(customer.isEmpty()) {
                throw new Exception("Tài khoản không tồn tại !");
            }
            if(!customer.get().isActive()) {
                throw new Exception("Tài khoản đã bị khóa !");
            }

            Customer existingCustomer = customer.get();
            return jwtUtils.generateToken(existingCustomer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Tài khoản hoặc mật khẩu không tồn tại : " + e.getMessage());
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
            return "Thay đổi mật khẩu thành công!";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Không thể thay đổi mật khẩu !");
        }
    }

    @Override
    public Customer getCustomerDetailsFromToken(String token) throws Exception {
        try {
            if(jwtUtils.isTokenExpired(token)) {
                throw new InvalidTokenException("Token đã hết hạn !");
            }

            String phoneNumber = jwtUtils.extractPhoneNumber(token);

            Optional<Customer> customerOpt = customerRepo.findCustomerByPhone(phoneNumber);
            if (customerOpt.isPresent()) {
                return customerOpt.get();
            } else {
                throw new InvalidTokenException("Không thể tìm thấy người dùng với số điện thoại : " + phoneNumber);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new InvalidTokenException("Token không tồn tại hoặc đã hết hạn!");
        }
    }

    @Override
    public Customer getCustomerDetailsFromRefreshToken(String refreshToken) throws Exception {
        try {
            Token token = tokenRepo.findByRefreshToken(refreshToken);

            if (token == null) {
                throw new InvalidTokenException("Refresh token không tồn tại!");
            }

            if (token.getRefreshExpirationDate().isBefore(LocalDateTime.now())) {
                throw new InvalidTokenException("Refresh token đã hết hạn!");
            }

            return customerRepo
                    .findCustomerByPhone(token.getCustomer().getPhone())
                    .orElseThrow(() -> new InvalidTokenException("Không thể tìm thấy người dùng với số điện thoại : " + token.getCustomer().getPhone()));
        }  catch (Exception e) {
            throw new InvalidTokenException("Lỗi không xác định khi xử lý refresh token!");
        }
    }

    public boolean isAdminFromToken(String token) throws Exception {
        try {
            Customer customer = getCustomerDetailsFromToken(token);

            return customer.getRole().getName().equals(Role.ROLE_ADMIN);
        } catch (Exception e) {
            throw new InvalidTokenException("Bạn không phải là quản trị viên!");
        }
    }

    @Override
    public Customer updateCustomer(Integer customerId, UpdateCustomerDTO customerUpadtedDTO) throws Exception {
        Customer existingCustomer = customerRepo.findById(customerId)
                .orElseThrow(() -> new Exception("Không thể tìm thấy người dùng với id " + customerId));

        String phoneNumber = customerUpadtedDTO.getPhone();
        if(existingCustomer.getPhone() != null && !existingCustomer.getPhone().equals(phoneNumber) && customerRepo.existsByPhone(phoneNumber)) {
            throw new DataIntegrityViolationException("Số điện thoại đã được sử dụng !");
        }

        String email = customerUpadtedDTO.getEmail();
        if(!existingCustomer.getEmail().equals(email) && customerRepo.existsByEmail(email)) {
            throw new DataIntegrityViolationException("Email đã được sử dụng !");
        }
        if (customerUpadtedDTO.getEmail() != null) {
            existingCustomer.setEmail(customerUpadtedDTO.getEmail());
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

                customerRepo.save(existingCustomer);
                return "Mật khẩu đã được thay đổi thành công !";
            } else {
                throw new Exception("Mật khẩu cũ không chính xác !");
            }
        } catch (BadCredentialsException e) {
            throw new Exception("Mật khẩu cũ không chính xác !");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Không thể thay đổi mật khẩu !");
        }
    }

    @Override
    @Transactional
    public String logout(String accessToken) throws Exception {
        try {
            tokenRepo.deleteTokenByAccessToken(accessToken);

            return "Đăng xuất thành công!";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Không thể đăng xuất !");
        }
    }

    @Override
    @Transactional
    public String logoutAllFromAccessToken(String accessToken) throws Exception {
        try {
            Customer customer = getCustomerDetailsFromToken(accessToken);
            tokenRepo.deleteAllTokensByCustomer(customer);

            return "Đăng xuất toàn bộ thành công!";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Không thể đăng xuất toàn bộ !");
        }
    }

    public boolean lockAccount(int id) throws Exception {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new Exception("Không thể tìm thấy người dùng với số id : " + id));;

        customer.setActive(false);
        customerRepo.save(customer);
        return true;
    }

    public boolean unlockAccount(int id) throws Exception {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new Exception("Không thể tìm thấy người dùng với số id : " + id));;

        customer.setActive(true);
        customerRepo.save(customer);
        return true;
    }
}
