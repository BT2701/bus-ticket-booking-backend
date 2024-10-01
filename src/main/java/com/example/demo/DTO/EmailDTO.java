package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDTO {
    @NotBlank(message = "Recipient must be required!")
    private String recipient;

    @NotBlank(message = "Subject must be required!")
    private String subject;

    @NotBlank(message = "Body must be required!")
    private String body;
}
