package com.example.demo.Repository;

import com.example.demo.Model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepo extends JpaRepository<Driver, Integer> {
}
