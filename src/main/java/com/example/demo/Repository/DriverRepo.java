package com.example.demo.Repository;

import com.example.demo.Model.Customer;
import com.example.demo.Model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepo extends JpaRepository<Driver, Integer> {
    Page<Driver> findAll(Pageable pageable);
    Driver findByPhone(String phone);
}
