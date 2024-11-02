package com.example.demo.Service;

import com.example.demo.DTO.CustomerDTO;
import com.example.demo.DTO.ForgotPasswordDTO;
import com.example.demo.DTO.UpdateCustomerDTO;
import com.example.demo.Model.Customer;
import com.example.demo.Model.ForgotPassword;
import com.example.demo.Model.Role;
import com.example.demo.Model.Token;
import com.example.demo.Repository.CustomerRepo;
import com.example.demo.Repository.ForgotPasswordRepo;
import com.example.demo.Repository.RoleRepo;
import com.example.demo.Repository.TokenRepo;
import com.example.demo.Utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService{
    private final CustomerRepo customerRepo;
    private final JwtUtils jwtUtils;

    private final TokenRepo tokenRepo;
    private final RoleRepo roleRepo;
    private final ForgotPasswordRepo forgotPasswordRepo;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public Page<Customer> getCustomers(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return customerRepo.findAll(pageable);
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
                throw new Exception("Tài khoản không tồn tại !");
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
            throw new Exception("Tài khoản hoặc mật khẩu không chính xác " + e.getMessage());
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
                throw new DateTimeException("Token đã hết hạn !");
            }

            String phoneNumber = jwtUtils.extractPhoneNumber(token);

            return customerRepo
                    .findCustomerByPhone(phoneNumber)
                    .orElseThrow(() -> new Exception("Không thể tìm thấy người dùng với số điện thoại : " + phoneNumber));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Token không đúng hoặc không tồn tại!");
        }
    }

    @Override
    public Customer getCustomerDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token token = tokenRepo.findByRefreshToken(refreshToken);

        if(token.getRefreshExpirationDate().isBefore(LocalDateTime.now())) {
            throw new DateTimeException("Refresh token đã hết hạn !");
        }

        return customerRepo
                .findCustomerByPhone(token.getCustomer().getPhone())
                .orElseThrow(() -> new Exception("Không thể tìm thấy người dùng với số điện thoại : " + token.getCustomer().getPhone()));
    }

    @Override
    public Customer updateCustomer(Integer customerId, UpdateCustomerDTO customerUpadtedDTO) throws Exception {
        Customer existingCustomer = customerRepo.findById(customerId)
                .orElseThrow(() -> new Exception("Không thể tìm thấy người dùng với id " + customerId));

        String phoneNumber = customerUpadtedDTO.getPhone();
        if(!existingCustomer.getPhone().equals(phoneNumber) && customerRepo.existsByPhone(phoneNumber)) {
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
