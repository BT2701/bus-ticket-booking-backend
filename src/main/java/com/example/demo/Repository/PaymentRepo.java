package com.example.demo.Repository;

import com.example.demo.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepo extends JpaRepository<Payment, Integer> {
    @Query("select p from payments p where p.booking.id=:bookingId")
    public Payment findByBooking(@Param("bookingId") int bookingId);
}
