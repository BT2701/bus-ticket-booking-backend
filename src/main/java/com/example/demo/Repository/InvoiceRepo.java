package com.example.demo.Repository;

import com.example.demo.Model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepo extends JpaRepository<Booking, Integer> {

    @Query("SELECT c.name as busType, s.departure, s.arrival, s, " +
            "bk.seatnum, r.duration, f.name as fromStation, t.name as toStation, bk.status, bk.id,p.id " +
            "FROM bookings bk " +
            "LEFT JOIN bk.payment p " + // Thêm LEFT JOIN với bảng payment
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
    Page<Object[]> findInvoiceByPhoneAndStatus(
            @Param("phoneNumber") String phoneNumber,
            @Param("statuses") List<String> statuses,
            Pageable pageable);

    @Query("SELECT c.name as busType, s.departure, s.arrival, s.price, " +
            "bk.seatnum, r.duration, f.name as fromStation, t.name as toStation, bk.status, bk.id, " +
            "CASE WHEN fb.id IS NULL THEN 0 ELSE 1 END as feedback " + // Kiểm tra xem có phản hồi hay không
            "FROM bookings bk " +
            "JOIN bk.customer cu " +
            "JOIN bk.schedule s " +
            "JOIN s.bus b " +
            "JOIN b.category c " +
            "JOIN s.route r " +
            "JOIN r.from f " +
            "JOIN r.to t " +
            "LEFT JOIN feedback fb ON fb.booking.id = bk.id " + // Left join với bảng feedback để kiểm tra phản hồi
            "WHERE cu.id = :customerId " + // Điều kiện id của customer
            "AND bk.status = 2 " + // Điều kiện status = 2 (đã sử dụng)
            "AND s.arrival < CURRENT_DATE " + // Điều kiện arrival nhỏ hơn ngày hôm nay
            "GROUP BY c.name, s.departure, s.arrival, s.price, bk.seatnum, r.duration, f.name, t.name, bk.id, fb.id")
    Page<Object[]> findBookingsByCustomerIdAndStatusAndArrivalBeforeToday(
            @Param("customerId") int customerId,
            Pageable pageable
    );

    // Cập nhật trạng thái vé
    @Modifying
    @Query("UPDATE bookings bk SET bk.status = 3 WHERE bk.id = :id") // 3 là mã trạng thái cho "đã hủy"
    void updateTicketStatus(@Param("id") Integer id);

    // Hàm lấy trạng thái của vé bằng ticketId
    @Query("SELECT bk.status FROM bookings bk WHERE bk.id = :id")
    Integer findStatusByTicketId(@Param("id") int id);

    @Query("SELECT c.name as busType, s.departure, s.arrival, s.price, " +
            "bk.seatnum, r.duration, f.name as fromStation, t.name as toStation, bk.status, bk.id,p.id "  +
            "FROM bookings bk " +
            "LEFT JOIN bk.payment p " + // Thêm LEFT JOIN với bảng payment
            "JOIN bk.customer cu " +
            "JOIN bk.schedule s " +
            "JOIN s.bus b " +
            "JOIN b.category c " +
            "JOIN s.route r " +
            "JOIN r.from f " +
            "JOIN r.to t " +
            "WHERE bk.id = :idBooking") // Tìm kiếm theo idBooking
    Object[] findInvoiceByBookingId(@Param("idBooking") int idBooking);

    @Modifying
    @Query("UPDATE bookings bk SET bk.status = :newStatus WHERE bk.id = :idBooking")
    void updateTicketStatusByBookingId(@Param("idBooking") Integer idBooking, @Param("newStatus") Integer newStatus);


}
