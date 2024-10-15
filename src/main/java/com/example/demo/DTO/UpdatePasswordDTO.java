package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordDTO {
    @NotBlank(message = "Mật khẩu cũ không được bỏ trống !")
    private String oldPassword;

    @NotBlank(message = "Mật khẩu mới không được bỏ trống !")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu mới không được bỏ trống !")
    private String confirmNewPassword;
}
