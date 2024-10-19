package com.example.demo.Controller;


import com.example.demo.Model.Schedule;
import com.example.demo.Service.ScheduleSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
public class ScheduleCTL {
    @Autowired
    private ScheduleSV scheduleSV;
    @GetMapping("api/schedules")
    public Map<String, Object> getSchedules(){
        Map<String, Object> results= new HashMap<>();
        results.put("schedules",scheduleSV.getAllSchedules());

        return results;
    }
    @GetMapping("api/schedulebus")
    public Map<String, Object> getSchedulesByBus(@RequestParam("busid") int busid){
        Map<String, Object> results= new HashMap<>();
        results.put("schedules",scheduleSV.getByBus(busid));
        return results;
    }
    @GetMapping("api/schedule")
    public Schedule getById(@RequestParam("id") int id){
        return scheduleSV.getScheduleById(id);
    }

    @GetMapping("api/schedule/driver/{driverId}")
    public ResponseEntity<List<Schedule>> getSchedulesByDriver(@PathVariable int driverId) {
        List<Schedule> schedules = scheduleSV.getSchedulesByDriverId(driverId);
        return ResponseEntity.ok(schedules);
    }
}
