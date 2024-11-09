package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity(name = "notifications")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "date_sent")
    private Timestamp date;
    @Column
    private String message;
    @Column
    private int status;
    @ManyToOne
    @JoinColumn(name = "customer")
    private Customer customer;
}
