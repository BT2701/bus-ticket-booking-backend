package com.example.demo.Repository;
import com.example.demo.Model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RouteRepo extends JpaRepository<Route, Integer> {

    // Lấy các địa điểm duy nhất từ cột 'from'
    @Query("SELECT DISTINCT s.address FROM routes r JOIN r.from s")
    List<String> findUniqueFromLocations();
    // Trả về danh sách các địa điểm duy nhất từ cột 'from'

    // Lấy các địa điểm duy nhất từ cột 'to'
    @Query("SELECT DISTINCT s.address FROM routes r JOIN r.to s")
    List<String> findUniqueToLocations();

    @Query("SELECT c.name as busType, s.departure, s.arrival, s.price, "
            + "(c.seat_count - COALESCE(SUM(CASE WHEN bk.status = 1 THEN 1 ELSE 0 END), 0)) as remainingSeats, "

            + "r.duration, f.name as fromStation, t.name as toStation, s.id  "
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
            + "CASE WHEN :sortParam = 'priceAsc' THEN s.price END ASC, "
            + "CASE WHEN :sortParam = 'priceDesc' THEN s.price END DESC")
    List<Object[]> findSchedulesWithDetails(@Param("fromAddress") String fromAddress,
                                            @Param("toAddress") String toAddress,
                                            @Param("departureDate") LocalDate departureDate,
                                            @Param("currentDateTimePlusOneHour") LocalDateTime currentDateTimePlusOneHour, // Thêm tham số này
                                            @Param("lowestPrice") Double lowestPrice,
                                            @Param("highestPrice") Double highestPrice,
                                            @Param("busTypes") List<String> busTypes,
                                            @Param("sortParam") String sortParam);

    @Query("SELECT c.name as busType, r.distance  , r.duration, s.price, f.name as fromStation, t.name as toStation,f.address,t.address "
            + "FROM schedules s "
            + "JOIN s.bus b "
            + "JOIN b.category c "
            + "JOIN s.route r "
            + "JOIN r.from f "
            + "JOIN r.to t "
            + "ORDER BY t.address ASC")
    List<Object[]> findAllBusRoutes();



    //LEFT JOIN cho phép bạn lấy tất cả các bản ghi từ bảng bên trái (schedules trong trường hợp này), ngay cả khi không có bản ghi tương ứng trong bảng bên phải (bookings).
    //Điều này có nghĩa là bạn sẽ nhận được tất cả các lịch trình, kể cả những lịch trình không có bất kỳ đặt chỗ nào.

    @Query(value = "SELECT r.id,r.distance, r.duration, sch.price,sFrom.address,sTo.address, COUNT(b.id) AS quantityTicket " +
            "            FROM routes r " +
            "            JOIN schedules sch ON r.id = sch.route " +
            "            JOIN bookings b ON sch.id = b.schedule " +
            "            JOIN stations sFrom ON sFrom.id = r.from" +
            "            JOIN stations sTo ON sTo.id = r.to" +
            "            WHERE b.time >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) " +
            "            GROUP BY r.id " +
            "ORDER BY `quantityTicket` DESC LIMIT :numLimit;"
            ,  nativeQuery = true)
    List<Object[]> findMostPopularRoute(@Param("numLimit") int numLimit);


}
