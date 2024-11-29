package com.example.demo.Controller;


import com.example.demo.Model.Schedule;
import com.example.demo.Service.ScheduleSV;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
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

    @GetMapping("api/schedule-management")
    public ResponseEntity<List<Schedule>> getSchedule(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(scheduleSV.getScheduleLimit(page, size));
    }

    @GetMapping("api/schedule/total")
    public ResponseEntity<Integer> getTotalSchedule() {
        return ResponseEntity.ok(scheduleSV.getTotalSchedule());
    }

    @PostMapping("api/schedule")
    public Schedule addSchedule(@Valid @RequestBody Schedule schedule) {
        return scheduleSV.addSchedule(schedule);
    }

    @PutMapping("api/schedule")
    public ResponseEntity<String> updateSchedule(@Valid @RequestBody Schedule schedule) {
        scheduleSV.updateSchedule(schedule);
        return ResponseEntity.ok("Schedule updated");
    }

    @DeleteMapping("api/schedule/{id}")
    public ResponseEntity<String> deleteSchedule(@PathVariable int id) {
        scheduleSV.deleteSchedule(id);
        return ResponseEntity.ok("Schedule deleted");
    }
}
