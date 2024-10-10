package com.example.demo.Service;

import com.example.demo.Model.Booking;
import com.example.demo.Model.Customer;
import com.example.demo.Model.Schedule;
import com.example.demo.Repository.BookingRepo;
import com.example.demo.Repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
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

}
