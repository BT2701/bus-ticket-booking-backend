package com.example.demo.Repository;
import com.example.demo.Model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface RouteRepo extends JpaRepository<Route, Integer> {
    @Query(value = "SELECT r.id,r.distance, r.duration, sch.price,sFrom.address,sTo.address, COUNT(b.id) AS quantityTicket " +
            "            FROM routes r " +
            "            JOIN schedules sch ON r.id = sch.route " +
            "            JOIN bookings b ON sch.id = b.schedule " +
            "            JOIN stations sFrom ON sFrom.id = r.from" +
            "            JOIN stations sTo ON sTo.id = r.to" +
            "            WHERE b.time >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) " +
            "            GROUP BY r.id " +
            "ORDER BY `quantityTicket` DESC LIMIT 3;"
            ,  nativeQuery = true)
    List<Object[]> findMostPopularRoute();
}
