package com.example.demo.Service;

import com.example.demo.DTO.PaymentDTO;
import com.example.demo.Model.Booking;
import com.example.demo.Model.Payment;
import com.example.demo.Repository.BookingRepo;
import com.example.demo.Repository.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class PaymentSV {
    @Autowired
    private PaymentRepo paymentRepo;
    @Autowired
    private BookingRepo bookingRepo;

    public void addPayment(PaymentDTO payment) {
        Booking b= bookingRepo.findById(payment.getBookingId()).orElse(null);
        Payment p= new Payment();
        Timestamp timestamp = Timestamp.from(java.time.Instant.now());
        p.setAmount(payment.getAmount());
        p.setBooking(b);
        p.setMethod(payment.getMethod());
        p.setTime(timestamp);
        paymentRepo.save(p);
    }
}
