package com.example.demo.DTO;

import com.example.demo.Model.Booking;
import com.example.demo.Model.Token;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    @NotBlank(message = "Name is required!")
    private String name;

    @NotBlank(message = "Address is required!")
    private String address;

    @NotBlank(message = "Phone is required!")
    @Size(min = 10, max = 10, message = "Phone number must be 10 characters")
    private String phone;

    @NotNull(message = "Date of birth is required!")
    private Date birth;

    @NotBlank(message = "Email is required!")
    private String email;

    @NotBlank(message = "Password is required!")
    private String password;

    @NotBlank(message = "Confirm password is required!")
    private String confirmPassword;
}
