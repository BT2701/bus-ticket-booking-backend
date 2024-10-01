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
    @NotBlank(message = "Reset Token is required!")
    private String resetToken;

    @NotBlank(message = "Password is required!")
    private String password;

    @NotBlank(message = "Confirm password is required!")
    private String confirmPassword;
}
