package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity(name = "forgotpassword")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="reset_token", nullable = true, length = 255)
    private String resetToken;

    @Column(name="reset_expiration", nullable = true, length = 255)
    private LocalDateTime resetExpirationDate;

    @OneToOne
    @JoinColumn(name = "customer")
    private Customer customer;
}
