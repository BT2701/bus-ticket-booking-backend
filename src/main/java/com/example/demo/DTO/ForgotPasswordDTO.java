package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordDTO {
    @NotBlank(message = "Reset Token không được bỏ trống!")
    private String resetToken;

    @NotBlank(message = "Mật khẩu mới không được bỏ trống!")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu mới không được bỏ trống!")
    private String confirmPassword;
}
