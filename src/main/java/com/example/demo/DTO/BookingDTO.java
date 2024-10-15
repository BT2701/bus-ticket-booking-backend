package com.example.demo.DTO;

import com.example.demo.Model.Schedule;
import lombok.*;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private String email;
    private String phone;
    private String name;
    private Schedule schedule;
    private List<String> seats;
}
