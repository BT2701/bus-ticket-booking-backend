package com.example.demo.Controller;

import com.example.demo.Model.Driver;
import com.example.demo.Service.DriverSV;
import com.example.demo.Service.FileSV;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverCTL {
    private final DriverSV driverSV;
    private final FileSV fileSV;

    private final String path = "uploads/driverImages/";

    @GetMapping("")
    public ResponseEntity<?> getAllDrivers(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Page<Driver> drivers = driverSV.getAllDrivers(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping(value = "/avatar/{fileName}")
    public void showCustomerAvatar(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        InputStream resourceFile = fileSV.getResourceFile(path, fileName);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(resourceFile, response.getOutputStream());
    }

    @PostMapping("")
    public ResponseEntity<?> addDriver(
            @RequestParam("name") String name,
            @RequestParam("license") String license,
            @RequestParam("phone") String phone,
            @RequestParam("file") MultipartFile file) {

        try {
            String imgPath = fileSV.uploadFile(path, file);

            Driver driver = new Driver();
            driver.setName(name);
            driver.setLicense(license);
            driver.setPhone(phone);
            driver.setImg(imgPath);

            Driver newDriver = driverSV.addDriver(driver);
            return ResponseEntity.ok(newDriver);
        } catch (IOException e) {
            return CustomersCTL.handleError(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDriver(
            @PathVariable int id,
            @RequestParam("name") String name,
            @RequestParam("license") String license,
            @RequestParam("phone") String phone,
            @RequestParam(value = "file", required = false) MultipartFile file)
    {
        String imgPath = null;
        if (file != null && !file.isEmpty()) {
            try {
                imgPath = fileSV.uploadFile(path, file);
            } catch (IOException e) {
                throw new RuntimeException("Bị lỗi khi upload file : " + e.getMessage());
            }
        }

        Driver updatedDriver = driverSV.updateDriver(id, name, license, phone, imgPath);
        return ResponseEntity.ok(updatedDriver);
    }


}
