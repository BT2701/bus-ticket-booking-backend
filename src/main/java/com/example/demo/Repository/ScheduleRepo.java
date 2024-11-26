package com.example.demo.Repository;

import com.example.demo.Model.Schedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepo extends JpaRepository<Schedule, Integer> {
    @Query("select s from schedules s where s.bus.id = :bus ")
    List<Schedule> findByBus(@Param("bus") int busId);

    @Query("SELECT s FROM schedules s WHERE s.bus.driver.id = :driverId AND s.departure >= CURRENT_TIMESTAMP")
    List<Schedule> findByDriverId(@Param("driverId") int driverId);

    @Query("select s from schedules s where s.route.id =:routeid")
    List<Schedule> findByRoute(@Param("routeid") int routeid);

    @Query("select s from schedules s")
    List<Schedule> getScheduleLimit(Pageable pageable);

    @Query("select s from schedules s where s.departure >= CURRENT_TIMESTAMP")
    List<Schedule> getAvailableSchedules();
}
