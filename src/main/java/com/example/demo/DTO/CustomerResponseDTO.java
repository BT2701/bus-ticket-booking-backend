package com.example.demo.DTO;

import com.example.demo.Model.Booking;
import com.example.demo.Model.Token;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {
    private int id;
    private String name;
    private String address;
    private Date birth;
    private String phone;
    private String email;
    private List<Booking> bookings;
}
