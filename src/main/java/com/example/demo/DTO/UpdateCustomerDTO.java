package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Date;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerDTO {
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
}
