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
    private String title;
    @Column
    private String message;
    @Column(name = "read_at")  // ánh xạ tới trường 'read_at' trong DB
    private Timestamp readAt;
    @ManyToOne
    @JoinColumn(name = "customer")
    private Customer customer;
}
