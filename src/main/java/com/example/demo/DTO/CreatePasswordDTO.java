package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePasswordDTO {
    @NotBlank(message = "Mật khẩu mới không được bỏ trống !")
    private String password;
}
