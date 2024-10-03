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
    @NotBlank(message = "Old password is required!")
    private String oldPassword;

    @NotBlank(message = "New password is required!")
    private String newPassword;

    @NotBlank(message = "Confirm new password is required!")
    private String confirmNewPassword;
}
