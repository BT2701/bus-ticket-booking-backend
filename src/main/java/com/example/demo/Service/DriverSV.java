package com.example.demo.Service;

import com.example.demo.Model.Driver;
import com.example.demo.Repository.DriverRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverSV {
    private final DriverRepo driverRepository;

    public ArrayList<Driver> getAllDrivers() {
        return (ArrayList<Driver>) driverRepository.findAll();
    }

    public Optional<Driver> getDriverById(int id) {
        return driverRepository.findById(id);
    }

    public Driver addDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    public Driver updateDriver(int id, String name, String license, String phone, String imgPath) {
        Optional<Driver> optionalDriver = driverRepository.findById(id);
        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get();
            driver.setName(name);
            driver.setLicense(license);
            driver.setPhone(phone);

            if (imgPath != null) {
                driver.setImg(imgPath);
            }

            return driverRepository.save(driver);
        } else {
            throw new RuntimeException("Không tìm thấy tài xế với id : " + id);
        }
    }
}
