package com.example.demo.Controller;

import com.example.demo.DTO.BookingDTO;
import com.example.demo.DTO.BookingManagementDTO;
import com.example.demo.Model.Customer;
import com.example.demo.Service.BookingSV;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingCTL {
    @Autowired
    private BookingSV bookingSV;
    @PostMapping("/booking")
    public ResponseEntity<String> booking(@Valid @RequestBody BookingDTO bookingDTO) {
        bookingSV.addBooking(bookingDTO.getEmail(), bookingDTO.getName(), bookingDTO.getPhone(), bookingDTO.getSchedule(), bookingDTO.getSeats());
        return ResponseEntity.ok("Booking started");
    }
    @GetMapping("/booking-management")
    public ResponseEntity<List<BookingManagementDTO>> getBooking(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(bookingSV.getAllBookingManagement(page, size));
    }
    @GetMapping("/booking/total")
    public ResponseEntity<Integer> getTotalBooking() {
        return ResponseEntity.ok(bookingSV.getTotalBooking());
    }
    @GetMapping("/user")
    public Customer getUser(@RequestParam String phone) {
        return bookingSV.getCustomerByPhone(phone);
    }
    @GetMapping("/booked-seats")
    public List<Object> getBookedSeats(@RequestParam int scheduleId) {
        return bookingSV.getSeatBySchedule(scheduleId);
    }

}
