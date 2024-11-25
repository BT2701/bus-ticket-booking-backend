package com.example.demo.Repository;

import com.example.demo.DTO.BookingManagementDTO;
import com.example.demo.Model.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Integer> {
    @Query("select b from bookings b where b.schedule.id=:scheduleId")
    public Booking findBySchedule(@Param("scheduleId")int scheduleId);

    @Query("select new com.example.demo.DTO.BookingManagementDTO(b.id, b.customer.name, b.customer.phone, b.customer.email, b.seatnum, b.time, p.id, b.schedule) " +
            "from bookings b left join payments p on b.id = p.booking.id")
    public List<BookingManagementDTO> getBookingManagement(Pageable pageable);

    @Query("select b.seatnum from bookings b where b.schedule.id=:scheduleId")
    public List<Object> getSeatBySchedule(@Param("scheduleId")int scheduleId);
}
