package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity(name = "ewalletpay")
@Data
public class Ewalletpay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private int transactionid;
    @Column
    private int status;
    @Column
    private Timestamp time;
    @Column
    private String provider;
    @OneToOne
    @JoinColumn(name = "payment")
    private Payment payment;
}
