package com.example.demo.Repository;

import com.example.demo.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Integer> {
    @Query("select b from bookings b where b.schedule.id=:scheduleId")
    public Booking findBySchedule(@Param("scheduleId")int scheduleId);
}
