package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity(name = "buses")
@Data
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String busnumber;
    @Column
    private int seatcount;
    @Column
    private String bustype;
    @Column
    private String img;
    @ManyToOne
    @JoinColumn(name= "driver")
    private Driver driver;
    @OneToMany(mappedBy = "bus")
    @JsonIgnore
    private List<Schedule> schedules;

}
