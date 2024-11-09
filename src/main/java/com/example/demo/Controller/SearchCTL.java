package com.example.demo.Controller;

import com.example.demo.Service.RouteSV; // Import service RouteSV
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api")
public class SearchCTL {
    @Autowired
    private RouteSV routeSV;

    // Endpoint để lấy danh sách các tuyến đường duy nhất
    @GetMapping("/get-from-to")
    public ResponseEntity<Map<String, List<String>>> getUniqueRoutes() {
        List<String> fromLocations = routeSV.getUniqueFromLocations(); // Gọi phương thức để lấy danh sách địa điểm 'from'
        List<String> toLocations = routeSV.findUniqueToLocations(); // Gọi phương thức để lấy danh sách địa điểm 'to'

        // Tạo một Map để chứa kết quả
        Map<String, List<String>> uniqueLocations = Map.of(
                "from", fromLocations,
                "to", toLocations
        );

        return ResponseEntity.ok(uniqueLocations); // Trả về danh sách địa điểm
    }
    @GetMapping("/get-all-routes")
    public ResponseEntity<List<Object[]>> findAllBusRoutes() {
        List<Object[]> routes = routeSV.findAllBusRoutes();
        return ResponseEntity.ok(routes);
    }

    // Endpoint cho việc tìm kiếm
    @GetMapping("/search")
    public ResponseEntity<List<Object[]>> searchRoutes(
            @RequestParam String pickup,
            @RequestParam String dropoff,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @RequestParam(required = false) Double lowestPrice,
            @RequestParam(required = false) Double highestPrice,
            @RequestParam(required = false) List<String> busTypes,
            @RequestParam(required = false) String sort) {

        // Lấy ngày giờ hiện tại và cộng thêm 1 giờ
        //để lấy các chuyến xe có thời đến sớm hơn hiện tại 1 tiếng
        LocalDateTime currentDateTimePlusOneHour = LocalDateTime.now().plus(1, ChronoUnit.HOURS);


        // Gọi dịch vụ để lấy lịch trình xe buýt
        List<Object[]> schedules = routeSV.getBusSchedules(
                pickup,
                dropoff,
                departureDate,
                lowestPrice,
                highestPrice,
                busTypes,
                sort,
                currentDateTimePlusOneHour);

        return ResponseEntity.ok(schedules);
    }

}
