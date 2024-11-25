package com.example.demo.Service;

import com.example.demo.DTO.BookingDTO;
import com.example.demo.DTO.BookingManagementDTO;
import com.example.demo.Model.Booking;
import com.example.demo.Model.Customer;
import com.example.demo.Model.Payment;
import com.example.demo.Model.Schedule;
import com.example.demo.Repository.BookingRepo;
import com.example.demo.Repository.CustomerRepo;
import com.example.demo.Repository.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class BookingSV {
    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private PaymentRepo paymentRepo;

    public Customer addCustomerForBooking(String email, String name, String phone) {
        Customer c= new Customer();
        if(customerRepo.existsByEmail(email)){
            c= customerRepo.findCustomerByEmail(email).orElse(null);
            return c;
        } else if (customerRepo.existsByPhone(phone)) {
            c= customerRepo.findCustomerByPhone(phone).orElse(null);
            return c;
        }
        else {
            c.setEmail(email);
            c.setName(name);
            c.setPhone(phone);
            customerRepo.save(c);
            return customerRepo.findCustomerByEmail(email).orElse(null);
        }
    }

    public boolean addBooking(String email, String name, String phone, Schedule schedule, List<String> seats) {
        Customer c= addCustomerForBooking(email, name, phone);
        Timestamp timestamp = Timestamp.from(Instant.now());
        int status=1;
        try {
            for (String seat : seats) {
                Booking booking = new Booking();
                booking.setCustomer(c);
                booking.setSchedule(schedule);
                booking.setStatus(status);
                booking.setTime(timestamp);
                booking.setSeatnum(seat);
                bookingRepo.save(booking);
            }
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public List<BookingManagementDTO> getAllBookingManagement(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookingRepo.getBookingManagement(pageable);
    }
    public int getTotalBooking() {
        return bookingRepo.findAll().size();
    }
    public Customer getCustomerByPhone(String phone) {
        return customerRepo.findCustomerByPhone(phone).orElse(null);
    }

    public List<Object> getSeatBySchedule(int scheduleId) {
        return bookingRepo.getSeatBySchedule(scheduleId);
    }
    public void updateBookingStatus(String id, BookingManagementDTO booking) {
        int bookingId= Integer.parseInt(id);
        Booking b= bookingRepo.findById(bookingId).orElse(null);
        if(b!=null){
            Customer c = addCustomerForBooking(booking.getEmail(), booking.getCustomerName(), booking.getPhone());
            b.setCustomer(c);
            b.setSeatnum(booking.getSeatNum());
            b.setSchedule(booking.getSchedule());
            bookingRepo.save(b);
        }
    }
    public void deleteBooking(String id) {
        int bookingId= Integer.parseInt(id);
        Booking b= bookingRepo.findById(bookingId).orElse(null);
        Payment p= paymentRepo.findByBooking(bookingId);
        if(b!=null){
            if(p!=null){
                paymentRepo.delete(p);
            }
            bookingRepo.delete(b);
        }
    }
}
