package com.example.demo.Controller;

import com.example.demo.Model.Bus;
import com.example.demo.Service.BusSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
public class BusCTL {
    @Autowired
    private BusSV busSV;

    @GetMapping("/api/buslist")
    public List<Bus> getBusList() {
        return busSV.getBuses();
    }
}
