package com.example.demo.DTO;
import com.example.demo.Model.Schedule;
import lombok.*;

import java.sql.Time;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoutePopularDTO {
    private int routeId;
    private int distance;
    private Time duration;
    private Schedule schedule;
    private String fromAddress;
    private String toAddress;
    private String fromName;
    private String toName;
    private long quantityTicket;
    private int rank;
}
