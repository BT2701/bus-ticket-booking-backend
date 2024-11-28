package com.example.demo.Controller;

import com.example.demo.DTO.AssignDriverToBusDTO;
import com.example.demo.DTO.BusDTO;
import com.example.demo.Model.Bus;
import com.example.demo.Service.BusSV;
import com.example.demo.Service.FileSV;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
@RequiredArgsConstructor
public class BusCTL {
    private final BusSV busSV;
    private final FileSV fileSV;

    @GetMapping("/api/buslist")
    public ResponseEntity<?> getBusList(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return ResponseEntity.ok(busSV.getBuses(pageNo, pageSize, sortBy, sortDir));
    }

    @PostMapping("/api/assignDriverToBus")
    public ResponseEntity<?> assignDriverToBus(@RequestBody AssignDriverToBusDTO assignDriverToBusDTO) {
        try {
            Bus bus = busSV.assignDriverToBus(assignDriverToBusDTO);

            return ResponseEntity.ok(bus);
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @PostMapping("/api/buses")
    public ResponseEntity<?> addBus(@ModelAttribute BusDTO bus) {
        try {
            Bus addedBus = busSV.addBus(bus);
            return ResponseEntity.ok(addedBus);
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @GetMapping(value = "/api/buses/img/{fileName}")
    public void showBusImage(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        InputStream resourceFile = fileSV.getResourceFile("uploads/busImages/", fileName);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(resourceFile, response.getOutputStream());
    }

    @PutMapping("/api/buses/{id}")
    public ResponseEntity<?> updateBus(@PathVariable Integer id, @ModelAttribute BusDTO bus) {
        try {
            Bus updatedBus = busSV.updateBus(id, bus);
            return ResponseEntity.ok(updatedBus);
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @DeleteMapping("/api/buses/{id}")
    public ResponseEntity<?> deleteBus(@PathVariable Integer id) {
        try {
            busSV.deleteBusById(id);
            return ResponseEntity.ok("Xóa xe bus thành công !");
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @GetMapping("/api/busesLimit")
    public ResponseEntity<?> getBusLimit(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(busSV.getBusLimit(page, size));
    }
}
