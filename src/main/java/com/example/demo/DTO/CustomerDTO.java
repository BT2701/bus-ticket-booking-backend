package com.example.demo.DTO;

import com.example.demo.Model.Booking;
import com.example.demo.Model.Token;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
    @NotBlank(message = "Tên không được bỏ trống !")
    private String name;

    @NotBlank(message = "Địa chỉ không được bỏ trống !")
    private String address;

    @NotBlank(message = "Số điện thoại không được bỏ trống !")
    @Size(min = 10, max = 10, message = "Số điện thoại phải là 10 số !")
    private String phone;

    @NotNull(message = "Ngày sinh không được bỏ trống !")
    private Date birth;

    @NotBlank(message = "Email không được bỏ trống !")
    @Email(message = "Không đúng định dạng email !")
    private String email;

    @NotBlank(message = "Mật khẩu không được bỏ trống !")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được bỏ trống !")
    private String confirmPassword;
}
