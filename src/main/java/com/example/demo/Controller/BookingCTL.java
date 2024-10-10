package com.example.demo.Controller;

import com.example.demo.DTO.BookingDTO;
import com.example.demo.Model.Booking;
import com.example.demo.Model.Schedule;
import com.example.demo.Service.BookingSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingCTL {
    @Autowired
    private BookingSV bookingSV;
    @PostMapping("/")
    public ResponseEntity<String> booking(@RequestBody BookingDTO bookingDTO) {
        bookingSV.addBooking(bookingDTO.getEmail(), bookingDTO.getName(), bookingDTO.getPhone(), bookingDTO.getSchedule(), bookingDTO.getSeats());
        return ResponseEntity.ok("Booking started");
    }

}
