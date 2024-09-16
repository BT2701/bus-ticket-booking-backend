package com.example.demo.Service;

import com.example.demo.Model.Bus;
import com.example.demo.Repository.BusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusSV {
    @Autowired
    private BusRepo busRepo;
    public List<Bus> getBuses() {
        return busRepo.findAll();
    }
}
