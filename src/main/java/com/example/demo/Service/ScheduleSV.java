package com.example.demo.Service;

import com.example.demo.Model.Schedule;
import com.example.demo.Repository.ScheduleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
    
@Service
public class ScheduleSV {
    @Autowired
    private ScheduleRepo scheduleRepo;

    public List<Schedule> getAllSchedules() {
        return scheduleRepo.findAll();
    }
    public List<Schedule> getByBus(int busid){
        return scheduleRepo.findByBus(busid);
    }
    public List<Schedule> getByRoute(int routeid){
        return scheduleRepo.findByRoute(routeid);
    }
    public Schedule getScheduleById(int id){
        return scheduleRepo.findById(id).orElse(null);
    }
    public List<Schedule> getSchedulesByDriverId(int driverId) {
        return scheduleRepo.findByDriverId(driverId);
    }
}
