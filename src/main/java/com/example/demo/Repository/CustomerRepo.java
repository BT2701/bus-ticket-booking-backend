package com.example.demo.Repository;

import com.example.demo.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {
    Optional<Customer> findCustomerByPhone (String username);

    Optional<Customer> findCustomerByEmail (String email);

    boolean existsByEmail (String email);

    boolean existsByPhone(String phone);
}
