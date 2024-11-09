package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity(name = "contact_us")
@Data
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String sender;
    @Column(nullable = false,length = 100)
    private String email;
    @Column(length = 10)
    private String phone;
    @Column(length = 255)
    private String title;
    @Column(nullable = false,length = 255)
    private String content;
    @Column
    private int status;
    @Column
    private Timestamp create_at;
    @Column
    private Timestamp update_at;

}
