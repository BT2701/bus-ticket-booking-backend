package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "buses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String busnumber;
    @Column
    private String img;
    @ManyToOne
    @JoinColumn(name= "driver")
    private Driver driver;
    @OneToMany(mappedBy = "bus")
    @JsonIgnore
    private List<Schedule> schedules;

    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;
}
