package com.example.demo.Repository;

import com.example.demo.Model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Integer> {
    Page<Customer> findAll(Pageable pageable);

    Optional<Customer> findCustomerByPhone (String username);

    Optional<Customer> findCustomerByEmail (String email);

    boolean existsByEmail (String email);

    boolean existsByPhone(String phone);

}
