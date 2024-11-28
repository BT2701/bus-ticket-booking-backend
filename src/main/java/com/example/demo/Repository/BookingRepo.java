package com.example.demo.Repository;

import com.example.demo.DTO.BookingManagementDTO;
import com.example.demo.DTO.BookingsToNotifyDTO;
import com.example.demo.Model.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Integer> {
    @Query("select b from bookings b where b.schedule.id=:scheduleId")
    public List<Booking> findBySchedule(@Param("scheduleId")int scheduleId);

    @Query("select new com.example.demo.DTO.BookingManagementDTO(b.id, b.customer.name, b.customer.phone, b.customer.email, b.seatnum, b.time, p.id, b.schedule) " +
            "from bookings b left join payments p on b.id = p.booking.id")
    public List<BookingManagementDTO> getBookingManagement(Pageable pageable);


    //Lấy doanh thu X ngày gần nhất (doanh thu toàn bộ hệ thống)
    @Query("SELECT DATE_FORMAT(b.time, '%Y-%m-%d') AS bookingDate , SUM(b.schedule.bus.category.price * b.schedule.route.distance) AS dailyRevenues " +
            "FROM bookings b " +
            "WHERE b.time >= :startDate AND b.status != 3 " +
            "GROUP BY DATE_FORMAT(b.time, '%Y-%m-%d') " +
            "ORDER BY bookingDate ASC")
    List<Object[]> findDailyRevenuesSinceDate(@Param("startDate") LocalDateTime startDate);

    //Doanh thu trong khoảng ngày X1 - X2 (doanh thu toàn bộ hệ thống)
    @Query("SELECT DATE_FORMAT(b.time, '%Y-%m-%d') AS bookingDate, SUM(b.schedule.bus.category.price * b.schedule.route.distance) AS dailyRevenues " +
            "FROM bookings b " +
            "WHERE b.time >= :startDate AND b.time <= :endDate AND b.status !=3" +
            "GROUP BY DATE_FORMAT(b.time, '%Y-%m-%d') " +
            "ORDER BY bookingDate ASC")
    List<Object[]> findDailyRevenuesBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    //Doanh thu theo X tháng gần nhất (doanh thu toàn bộ hệ thống)
    @Query("SELECT DATE_FORMAT(b.time, '%Y-%m') AS month, SUM(b.schedule.bus.category.price * b.schedule.route.distance)" +
            "FROM bookings b " +
            "WHERE b.time >= :startDate AND b.status !=3 " +
            "GROUP BY DATE_FORMAT(b.time, '%Y-%m') " +
            "ORDER BY month ASC")
    List<Object[]> findMonthlyRevenuesSinceDate(@Param("startDate") LocalDateTime startDate);

    //Lấy doanh thu X ngày gần nhất (doanh thu theo tuyến xe)
    @Query("SELECT DATE_FORMAT(b.time, '%Y-%m-%d') bookingDate, CONCAT(b.schedule.route.from.name, '-', b.schedule.route.to.name), SUM(b.schedule.bus.category.price * b.schedule.route.distance) "+
            "FROM bookings b " +
            "WHERE b.time >= :startDate AND b.status !=3 " +
            "GROUP BY b.schedule.route.id, DATE_FORMAT(b.time, '%Y-%m-%d') " +
            "ORDER BY bookingDate ASC ")
    List<Object[]> findDailyRevenuesByRouteWithStationsSinceDate(@Param("startDate") LocalDateTime startDate);

    //Doanh thu trong khoảng ngày X1 - X2 (doanh thu theo tuyến )
    @Query("SELECT DATE_FORMAT(b.time, '%Y-%m-%d') bookingDate, CONCAT(b.schedule.route.from.name, '-', b.schedule.route.to.name), SUM(b.schedule.bus.category.price * b.schedule.route.distance) "+
            "FROM bookings b "+
            "WHERE b.time >= :startDate AND b.time <= :endDate AND b.status !=3 " +
            "GROUP BY b.schedule.route.id, DATE_FORMAT(b.time, '%Y-%m-%d') " +
            "ORDER BY bookingDate ASC ")
    List<Object[]> findDailyRevenuesByRouteWithStationsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                                                    @Param("endDate") LocalDateTime endDate);

    //Lấy doanh thu X tháng gần nhất (doanh thu theo tuyến xe)
    @Query("SELECT DATE_FORMAT(b.time, '%Y-%m') AS month, CONCAT(b.schedule.route.from.name, '-', b.schedule.route.to.name), SUM(b.schedule.bus.category.price * b.schedule.route.distance) " +
            "FROM bookings b " +
            "WHERE b.time >= :startDate  AND b.status !=3 " +
            "GROUP BY b.schedule.route.id, DATE_FORMAT(b.time, '%Y-%m') " +
            "ORDER BY month ASC ")
    List<Object[]> findMonthlyRevenuesByRouteWithStationsSinceDate(@Param("startDate") LocalDateTime startDate);


    //Lấy vé đã đặt: với điều kiện chưa thanh toán hoặc đã thanh toán rồi (để thống kê xem tình trạng vé)
    @Query("SELECT " +
            "SUM(CASE WHEN p.id IS NOT NULL AND b.status = 1 THEN 1 ELSE 0 END) AS DaThanhToanChuaSuDung, " +
            "SUM(CASE WHEN p.id IS NULL AND b.status = 1 THEN 1 ELSE 0 END) AS ChuaThanhToan " +
            "FROM bookings b " +
            "LEFT JOIN b.payment p " +
            "WHERE b.status = 1")
    List<Object[]> tinhTrangVe();

    //Duyệt vé để lấy những customer cần được thông báo thanh toán và lịch trình chuẩn bị chạy
    //Vì cả hai đều trước 30p lịch khởi hành mới thông báo nên gộp chung
    @Query("SELECT new com.example.demo.DTO.BookingsToNotifyDTO(b, " +
            "CASE " +
            "    WHEN COUNT(p) = COUNT(b) THEN 1 " +
            "    WHEN COUNT(p) = 0 THEN 0 " +
            "    ELSE 0 " +
            "END) " +
            "FROM bookings b " +
            "LEFT JOIN b.payment p " +
            "WHERE b.schedule.departure BETWEEN :timeStart AND :timeEnd " +
            "GROUP BY b.customer, b.schedule")
    List<BookingsToNotifyDTO> listBookingToNotify(@Param("timeStart") Timestamp timeStart,
                                                  @Param("timeEnd") Timestamp timeEnd);




    @Query("select b.seatnum from bookings b where b.schedule.id=:scheduleId")
    public List<Object> getSeatBySchedule(@Param("scheduleId")int scheduleId);


    //Lấy nhưng customer đã đặt lịch trình X để thông báo lịch trình bị thay đổi.
    @Query("SELECT b FROM bookings b WHERE b.schedule.id = :scheduleId GROUP BY b.customer.id, b.schedule.id")
    List<Booking> bookingsToNotiChangeSchedule(@Param("scheduleId") int scheduleId);
}
