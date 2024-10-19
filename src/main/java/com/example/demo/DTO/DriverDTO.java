package com.example.demo.DTO;

import com.example.demo.Model.Bus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Data
@Builder
public class DriverDTO {
    private int id;

    @NotBlank(message = "Tên không được để trống!")
    private String name;

    @NotBlank(message = "Loại bằng lái không được để trống!")
    private String license;

    @NotBlank(message = "Số điện thoại không được để trống!")
    private String phone;

    @NotNull(message = "Ảnh không được để trống!")
    private MultipartFile img;
}
