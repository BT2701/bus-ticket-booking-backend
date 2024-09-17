package com.example.demo.Controller;


import com.example.demo.Service.ScheduleSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ScheduleCTL {
    @Autowired
    private ScheduleSV scheduleSV;
    @GetMapping("api/schedules")
    public Map<String, Object> getSchedules(){
        Map<String, Object> results= new HashMap<>();
        results.put("schedules",scheduleSV.getAllSchedules());

        return results;
    }
    @GetMapping("api/schedule")
    public Map<String, Object> getSchedulesByBus(@RequestParam("busid") int busid){
        Map<String, Object> results= new HashMap<>();
        results.put("schedules",scheduleSV.getByBus(busid));
        return results;
    }
}
