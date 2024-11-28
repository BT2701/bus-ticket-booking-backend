package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleAssignmentDTO {
    @NotNull(message = "Id của người dùng không được để trống !")
    private Integer customerId;

    @NotNull(message = "Tên của role không được để trống !")
    private String roleName;
}
