package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class LoginDTO {
    @NotBlank(message = "Tài khoản không được bỏ trống !")
    @JsonProperty("username")
    private String emailOrPhone;

    @NotBlank(message = "Mật khẩu không được bỏ trống !")
    private String password;
}
