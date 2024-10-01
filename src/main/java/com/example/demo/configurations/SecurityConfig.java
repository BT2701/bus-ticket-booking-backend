package com.example.demo.configurations;

import com.example.demo.Repository.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomerRepo customerRepo;

//    @Bean
//    public UserDetailsService userDetailsService() {
//        return phoneNumber -> customerRepo
//                .findCustomerByPhone(phoneNumber)
//                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with phone number " + phoneNumber));
//    };
    @Bean
    public UserDetailsService userDetailsService() {
        return identifier -> {
            // Kiểm tra xem identifier là email hay số điện thoại
            if (isEmail(identifier)) {
                return customerRepo
                        .findCustomerByEmail(identifier)
                        .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with email " + identifier));
            } else {
                return customerRepo
                        .findCustomerByPhone(identifier)
                        .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with phone number " + identifier));
            }
        };
    }
    private boolean isEmail(String identifier) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return identifier.matches(emailPattern);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
