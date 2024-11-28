package com.example.demo.Repository;
import com.example.demo.Model.Route;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Page;


@Repository
public interface RouteRepo extends JpaRepository<Route, Integer> {

    // Lấy các địa điểm duy nhất từ cột 'from'
    @Query("SELECT DISTINCT s.address FROM routes r JOIN r.from s")
    List<String> findUniqueFromLocations();
    // Trả về danh sách các địa điểm duy nhất từ cột 'from'

    // Lấy các địa điểm duy nhất từ cột 'to'
    @Query("SELECT DISTINCT s.address FROM routes r JOIN r.to s")
    List<String> findUniqueToLocations();

    @Query("SELECT c.name as busType, s.departure, s.arrival, s, "
            + "(c.seat_count - COALESCE(SUM(CASE WHEN bk.status = 1 THEN 1 ELSE 0 END), 0)) as remainingSeats, "
            + "r.duration, f.name as fromStation, t.name as toStation, s.id "
            + "FROM schedules s "
            + "JOIN s.bus b "
            + "JOIN b.category c "
            + "JOIN s.route r "
            + "JOIN r.from f "
            + "JOIN r.to t "
            + "LEFT JOIN bookings bk ON s.id = bk.schedule.id "
            + "WHERE f.address = :fromAddress AND t.address = :toAddress "
            + "AND DATE(s.departure) = :departureDate "
            + "AND s.departure >= :currentDateTimePlusOneHour "
            + "AND (:lowestPrice IS NULL OR s.price >= :lowestPrice) "
            + "AND (:highestPrice IS NULL OR s.price <= :highestPrice) "
            + "AND (:busTypes IS NULL OR c.name IN :busTypes) "
            + "GROUP BY c.name, s.departure, s.arrival, s.price, c.seat_count, r.duration, f.name, t.name "
            + "ORDER BY "
            + "CASE WHEN :sortParam IS NULL OR :sortParam = '' OR :sortParam = 'earliestDeparture' THEN s.departure END ASC, "
            + "CASE WHEN :sortParam = 'latestDeparture' THEN s.departure END DESC, "
            + "CASE WHEN :sortParam = 'priceAsc' THEN c.price END ASC, "
            + "CASE WHEN :sortParam = 'priceDesc' THEN c.price END DESC")
    List<Object[]> findSchedulesWithDetails(@Param("fromAddress") String fromAddress,
                                            @Param("toAddress") String toAddress,
                                            @Param("departureDate") LocalDate departureDate,
                                            @Param("currentDateTimePlusOneHour") LocalDateTime currentDateTimePlusOneHour, // Thêm tham số này
                                            @Param("lowestPrice") Double lowestPrice,
                                            @Param("highestPrice") Double highestPrice,
                                            @Param("busTypes") List<String> busTypes,
                                            @Param("sortParam") String sortParam);

    //LEFT JOIN cho phép bạn lấy tất cả các bản ghi từ bảng bên trái (schedules trong trường hợp này), ngay cả khi không có bản ghi tương ứng trong bảng bên phải (bookings).
    //Điều này có nghĩa là bạn sẽ nhận được tất cả các lịch trình, kể cả những lịch trình không có bất kỳ đặt chỗ nào.

    @Query("SELECT DISTINCT c.name as busType, r.distance, r.duration, s, f.name as fromStation, t.name as toStation" +
            ", f.address, t.address "
            + "FROM schedules s "
            + "JOIN s.bus b "
            + "JOIN b.category c "
            + "JOIN s.route r "
            + "JOIN r.from f "
            + "JOIN r.to t "
            + "ORDER BY t.address ASC")
    Page<Object[]> findAllBusRoutes(Pageable pageable);//thêm chức năng phân trang

    //Hàm lấy thông tin tuyến xe phổ biến (dựa vào vé đã bán, chỉ tính vé đã bán đã được sử dụng status = 2 và chưa sử dụng status = 1, khôgn tính vẽ đã bị hủy status = 3)
    @Query("SELECT r.id,r.distance, r.duration, sch ,sFrom.address, sTo.address, sFrom.name, sTo.name, COUNT(b.id) AS quantityTicket " +
            "FROM bookings b " +
            "JOIN b.schedule sch " +
            "JOIN sch.route r " +
            "JOIN r.from sFrom " +
            "JOIN r.to sTo " +
            "WHERE b.time >= :startDate AND b.status != 3" +
            "GROUP BY r.id " +
            "ORDER BY quantityTicket DESC")
    Page<Object[]> findMostPopularRoute(Pageable pageable,@Param("startDate") LocalDateTime startDate);


    //Hàm lấy thông tin tuyến xe phổ biến trong khoảng ngày X1-X2
    @Query("SELECT r.id,r.distance, r.duration, sch ,sFrom.address, sTo.address, sFrom.name, sTo.name, COUNT(b.id) AS quantityTicket " +
            "FROM bookings b " +
            "JOIN b.schedule sch " +
            "JOIN sch.route r " +
            "JOIN r.from sFrom " +
            "JOIN r.to sTo " +
            "WHERE b.time >= :startDate AND b.time <= :endDate AND b.status != 3" +
            "GROUP BY r.id " +
            "ORDER BY quantityTicket DESC")
    List<Object[]> findMostPopularRouteXtoX(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM routes r")
    List<Route> getRouteLimit(Pageable pageable);

}
