package com.example.demo.DTO;


import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private double amount;
    private String method;
    private int bookingId;
}
