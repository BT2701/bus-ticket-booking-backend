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
    @NotBlank(message = "Tên không được bỏ trống !")
    private String name;

    @NotBlank(message = "Địa chỉ không được bỏ trống !")
    private String address;

    @NotBlank(message = "Số điện thoại không được bỏ trống !")
    @Size(min = 10, max = 10, message = "Phone number must be 10 characters")
    private String phone;

    @NotNull(message = "Ngày sinh không được bỏ trống !")
    private Date birth;

    @NotBlank(message = "Email không được bỏ trống !")
    private String email;
}
