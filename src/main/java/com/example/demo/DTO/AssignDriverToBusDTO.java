package com.example.demo.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssignDriverToBusDTO {
    private int driverId;
    private int busId;
}
