package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name="tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="access_token", nullable = false, length = 255)
    private String token;

    @Column(name="expiration_date")
    private LocalDateTime accessExpirationDate;

    private boolean isAccessExpired;

    @Column(name="refresh_token", nullable = false, length = 255)
    private String refreshToken;

    @Column(name="refresh_expiration")
    private LocalDateTime refreshExpirationDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer")
    private Customer customer;
}
