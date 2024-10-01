package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity(name = "stations")
@Data
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column( nullable = false)
    private String name;
    @Column
    private String address;

    @OneToMany(mappedBy = "from")
    @JsonIgnore
    private List<Route> froms;

    @OneToMany(mappedBy = "to")
    @JsonIgnore
    private List<Route> tos;
}
