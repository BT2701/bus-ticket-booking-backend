package com.example.demo.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseDTO {
    private String message;
    private long status;
    private Object data;
}
