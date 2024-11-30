package com.example.demo.Service;

import com.example.demo.Model.Customer;
import com.example.demo.Model.Driver;
import com.example.demo.Repository.DriverRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverSV {
    private final DriverRepo driverRepository;

    public Page<Driver> getAllDrivers(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return driverRepository.findAll(pageable);
    }

    public Optional<Driver> getDriverById(int id) {
        return driverRepository.findById(id);
    }

    public Driver addDriver(Driver driver) throws Exception {
        Driver existing = driverRepository.findByPhone(driver.getPhone());
        if(existing != null) {
            throw new Exception("Số điện thoại đã tồn tại");
        }

        return driverRepository.save(driver);
    }

    public Driver updateDriver(int id, String name, String license, String phone, String imgPath) throws Exception {
        Optional<Driver> optionalDriver = driverRepository.findById(id);
        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get();
            driver.setName(name);
            driver.setLicense(license);
            driver.setPhone(phone);

            Driver existing = driverRepository.findByPhone(phone);
            if(existing != null && existing.getId() != id) {
                throw new Exception("Số điện thoại đã tồn tại");
            }

            if (imgPath != null) {
                driver.setImg(imgPath);
            }

            return driverRepository.save(driver);
        } else {
            throw new Exception("Không tìm thấy tài xế với id : " + id);
        }
    }
}
