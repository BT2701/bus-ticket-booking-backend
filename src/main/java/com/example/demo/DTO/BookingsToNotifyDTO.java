package com.example.demo.DTO;


import com.example.demo.Model.Booking;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingsToNotifyDTO {
    private Booking booking;
    private int isPayment;
}
