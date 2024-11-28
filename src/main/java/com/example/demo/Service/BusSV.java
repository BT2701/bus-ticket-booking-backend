package com.example.demo.Service;

import com.example.demo.DTO.AssignDriverToBusDTO;
import com.example.demo.DTO.BusDTO;
import com.example.demo.Model.Bus;
import com.example.demo.Model.Category;
import com.example.demo.Model.Driver;
import com.example.demo.Repository.BusRepo;
import com.example.demo.Repository.CategoryRepo;
import com.example.demo.Repository.DriverRepo;
import com.example.demo.Repository.ScheduleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class BusSV {
    @Autowired
    private BusRepo busRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private DriverRepo driverRepo;
    @Autowired
    private ScheduleRepo scheduleRepo;
    @Autowired
    private FileSV fileSV;

    private final String path = "uploads/busImages/";

    public Page<Bus> getBuses(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return busRepo.findAll(pageable);
    }
    public Bus getBus(int id) {
        return busRepo.findById(id).get();
    }

    public Bus assignDriverToBus(AssignDriverToBusDTO assignDriverToBusDTO) {
        Optional<Driver> existingDriver = driverRepo.findById(assignDriverToBusDTO.getDriverId());
        if(existingDriver.isEmpty()) {
            throw new RuntimeException("Không tìm thấy tài xe với ID: " + assignDriverToBusDTO.getDriverId());
        }
        Optional<Bus> existingBus = busRepo.findById(assignDriverToBusDTO.getBusId());
        if(existingBus.isEmpty()) {
            throw new RuntimeException("Không tìm thấy xe với ID: " + assignDriverToBusDTO.getBusId());
        }
        existingBus.get().setDriver(existingDriver.get());

        return busRepo.save(existingBus.get());
    }

    public Bus addBus(BusDTO busDTO) throws IOException {
        Driver driver = driverRepo.findById(busDTO.getDriverId()).orElseThrow(
                () -> new RuntimeException("Không tìm thấy tài xế với ID: " + busDTO.getDriverId())
        );
        Category category = categoryRepo.findById(busDTO.getCategoryId()).orElseThrow(
                () -> new RuntimeException("Không tìm thấy loại xe với ID: " + busDTO.getCategoryId())
        );

        String image = fileSV.uploadFile(path, busDTO.getImg());

        Bus bus = Bus.builder()
                .img(image)
                .category(category)
                .driver(driver)
                .busnumber(busDTO.getBusnumber())
                .build();

        return busRepo.save(bus);
    }

    public Bus updateBus(int busId, BusDTO busDTO) throws IOException {
        Bus bus = busRepo.findById(busId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy xe với ID: " + busId)
        );

        if(busDTO.getBusnumber() != null && !busDTO.getBusnumber().isEmpty()) {
            bus.setBusnumber(busDTO.getBusnumber());
        }
        if(busDTO.getImg() != null) {
            String busImg = fileSV.uploadFile(path, busDTO.getImg());
            bus.setImg(busImg);
        }
        if(busDTO.getDriverId() >= 0) {
            Driver driver = driverRepo.findById(busDTO.getDriverId()).orElseThrow(
                    () -> new RuntimeException("Không tìm thấy tài xế với ID: " + busDTO.getDriverId())
            );
            bus.setDriver(driver);
        }
        if(busDTO.getCategoryId() >= 0) {
            Category category = categoryRepo.findById(busDTO.getCategoryId()).orElseThrow(
                    () -> new RuntimeException("Không tìm thấy loại xe với ID: " + busDTO.getCategoryId())
            );
            bus.setCategory(category);
        }

        return busRepo.save(bus);
    }

    @Transactional
    public void deleteBusById(int id) {
        try {
            Bus bus = busRepo.findById(id).orElse(null);
            if (bus!= null) {
                scheduleRepo.deleteByBusId(id);
                busRepo.delete(bus);
            }
        } catch (Exception e) {
            throw new RuntimeException("Không thể xóa xe với ID: " + id + " vì : " + e.getMessage());
        }
    }
    public List<Bus> getBusLimit(int page, int size) {
        return busRepo.getAllBuses(PageRequest.of(page, size));
    }
}
