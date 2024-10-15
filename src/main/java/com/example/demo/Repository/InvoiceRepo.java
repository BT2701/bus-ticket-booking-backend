package com.example.demo.Repository;

import com.example.demo.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepo extends JpaRepository<Booking, Integer> {

    @Query("SELECT c.name as busType, s.departure, s.arrival, s.price, " +
            "bk.seatnum, r.duration, f.name as fromStation, t.name as toStation, bk.status, bk.id " +
            "FROM bookings bk " +
            "JOIN bk.customer cu " +
            "JOIN bk.schedule s " +
            "JOIN s.bus b " +
            "JOIN b.category c " +
            "JOIN s.route r " +
            "JOIN r.from f " +
            "JOIN r.to t " +
            "WHERE cu.phone = :phoneNumber " +
            "AND (bk.status IN :statuses) " + // Sử dụng IN để kiểm tra các giá trị status
            "GROUP BY c.name, s.departure, s.arrival, s.price, bk.seatnum, r.duration, f.name, t.name")
    List<Object[]> findInvoiceByPhoneAndStatus(@Param("phoneNumber") String phoneNumber, @Param("statuses") List<String> statuses);

}
