package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String seatnum;
    @Column
    private Timestamp time;
    @Column
    private int status;
    @ManyToOne
    @JoinColumn(name = "customer")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "schedule")
    @JsonIgnore
    private Schedule schedule;
    @OneToOne(mappedBy = "booking")
    @JsonIgnore
    private Payment payment;
}
