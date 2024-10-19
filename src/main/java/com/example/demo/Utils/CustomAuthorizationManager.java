package com.example.demo.Utils;

import com.example.demo.Model.Customer;
import com.example.demo.Repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

@Component
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final CustomerRepo customerRepository;

    @Autowired
    public CustomAuthorizationManager(CustomerRepo customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        Authentication auth = authentication.get();
        String requestUri = object.getRequest().getRequestURI();

        System.out.println("Request URI: " + requestUri);

        // cho qua các request không kiểm tra tài khoản active vì chưa đăng nhập
        if (requestUri.startsWith("/api/customers/login") || requestUri.startsWith("/api/customers/register")) {
            return new AuthorizationDecision(true);
        }

        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        String username = auth.getName();
        Optional<Customer> customer = customerRepository.findCustomerByPhone(username);

        if (customer.isPresent()) {
            boolean isAllowed = customer.get().isActive();

            if (!isAllowed && !requestUri.equals("/api/buslist")) {
                return new AuthorizationDecision(false);
            }
            return new AuthorizationDecision(true);
        } else {
            return new AuthorizationDecision(false);
        }
    }
}

