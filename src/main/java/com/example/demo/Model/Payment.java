package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Entity(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private double amount;
    @Column
    private String method;
    @Column
    private Timestamp time;
    @OneToOne
    @JoinColumn(name = "booking")
    private Booking booking;
    @OneToOne(mappedBy = "payment")
    @JsonIgnore
    private Ewalletpay ewalletpay;
}
