package com.example.demo.DTO;

import com.example.demo.Model.Payment;
import com.example.demo.Model.Schedule;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingManagementDTO {
    private Integer bookingId;
    private String customerName;
    private String phone;
    private String seatNum;
    private String time;
    private Integer payment;
    private Schedule schedule;
    public BookingManagementDTO(Integer bookingId, String customerName, String phone, String seatNum, Timestamp time, Integer payment, Schedule schedule) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.phone = phone;
        this.seatNum = seatNum;
        this.time = time.toString();
        this.payment = payment;
        this.schedule = schedule;
    }
}
