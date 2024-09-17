package com.example.demo.Repository;

import com.example.demo.Model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepo extends JpaRepository<Schedule, Integer> {
    @Query("select s from schedules s where s.bus.id = :bus ")
    List<Schedule> findByBus(@Param("bus") int busId);


    @Query("select s from schedules s where s.route.id =:routeid")
    List<Schedule> findByRoute(@Param("routeid") int routeid);
}
