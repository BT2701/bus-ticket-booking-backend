package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Entity(name = "schedules")
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private Timestamp departure;
    @Column
    private Timestamp arrival;
    @Column
    private double price;
    @ManyToOne
    @JoinColumn(name = "bus")
    private Bus bus;
    @ManyToOne
    @JoinColumn(name = "route")
    private Route route;
    @OneToMany(mappedBy = "schedule")
    private List<Booking> bookings;
}
