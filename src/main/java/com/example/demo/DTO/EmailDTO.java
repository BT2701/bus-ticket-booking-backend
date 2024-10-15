package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDTO {
    @NotBlank(message = "Người nhận không được bỏ trống!")
    private String recipient;

    @NotBlank(message = "Tiêu đề không được bỏ trống!")
    private String subject;

    @NotBlank(message = "Nội dung không được bỏ trống!")
    private String body;
}
