package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.util.List;

@Entity(name = "routes")
@Data
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String from;
    @Column
    private String to;
    @Column
    private int distance;
    @Column
    private Time duration;
    @OneToMany(mappedBy = "route")
    @JsonIgnore
    private List<Schedule> schedules;
}
