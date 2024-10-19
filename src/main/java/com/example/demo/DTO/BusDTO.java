package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class BusDTO {
    @NotBlank(message = "Biển số xe không được bỏ trống!")
    private String busnumber;

    @NotNull(message = "Ảnh của xe không được bỏ trống!")
    private MultipartFile  img;

    @NotNull(message = "Id tài xế không được bỏ trống!")
    private int driverId;

    @NotNull(message = "Id loại xe không được bỏ trống!")
    private int categoryId;
}
