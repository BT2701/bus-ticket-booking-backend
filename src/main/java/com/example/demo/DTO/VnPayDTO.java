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
public class VnPayDTO {
    private String email;
    private String phone;
    private String name;
    private Schedule schedule;
    private List<String> seats;
    private String method;
    private String provider;
    private String transactionId;
}
