package com.example.demo.Repository;

import com.example.demo.Model.Bus;
import com.example.demo.Model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepo extends JpaRepository<Bus, Integer> {
    Page<Bus> findAll(Pageable pageable);
}
