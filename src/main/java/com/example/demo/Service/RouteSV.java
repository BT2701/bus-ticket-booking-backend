package com.example.demo.Service;

import com.example.demo.Model.Route;
import com.example.demo.Repository.RouteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class RouteSV {
    @Autowired
    private RouteRepo routeRepo;

    // Phương thức để lấy danh sách các tuyến đường duy nhất
    public List<String> getUniqueFromLocations() {
        return routeRepo.findUniqueFromLocations();
    }
    public List<String> findUniqueToLocations() {
        return routeRepo.findUniqueToLocations();
    }
    // Cập nhật phương thức getBusSchedules để chấp nhận các tham số bổ sung
    public List<Object[]> getBusSchedules(String pickup, String dropoff, LocalDate departureDate,
                                          Double lowestPrice, Double highestPrice,
                                          List<String> busTypes, String sortParam,
                                          LocalDateTime currentDateTimePlusOneHour) { // Thêm tham số mới
        return routeRepo.findSchedulesWithDetails(pickup, dropoff, departureDate,
                currentDateTimePlusOneHour, // Đặt đúng vị trí tham số
                lowestPrice, highestPrice, busTypes, sortParam);
    }

    public Map<String, Object> findAllBusRoutes(int limit, int offset) {
        // Tính toán số trang từ offset và limit
        int page = offset / limit;
        Pageable pageable = PageRequest.of(page, limit);

        // Gọi repository để lấy dữ liệu phân trang
        Page<Object[]> pageResult = routeRepo.findAllBusRoutes(pageable);

        // Chuẩn bị phản hồi trả về với dữ liệu và thông tin phân trang
        Map<String, Object> response = new HashMap<>();
        response.put("data", pageResult.getContent()); // Dữ liệu của trang hiện tại
        response.put("totalElements", pageResult.getTotalElements()); // Tổng số bản ghi
        response.put("currentPage", pageResult.getNumber()); // Số trang hiện tại
        response.put("totalPages", pageResult.getTotalPages()); // Tổng số trang

        return response;
    }



    public  Map<String, Object> getMostPopularRoute(int pageNumber, int numLimit) {
        try {
            // Ngày bắt đầu lấy là 1 tháng trước đó kể từ ngày hiện tại
            LocalDateTime startDate = LocalDateTime.now()
                    .minusMonths(1)       // Trừ 1 tháng
                    .truncatedTo(ChronoUnit.DAYS); // Đặt thời gian về đầu ngày (00:00:00)

            // Tính toán số lượng item cần bỏ qua (offset)
            int offset = pageNumber * 3;
            // Nếu offset >= numLimit, không có gì để lấy, trả về null
            if (offset >= numLimit) {
                return null;
            }

            // Tính toán số lượng item cần lấy trong trang hiện tại
            // Lấy tối đa 3 hoặc số còn lại. Tại vì trên frontend chỉ có 3 item trong 1 trang.
            int limit = Math.min(3, numLimit - offset);

            // Tạo Pageable cho truy vấn phân trang
            Pageable pageable = PageRequest.of(0, offset + limit, Sort.by(Sort.Order.desc("quantityTicket")));
            Page<Object[]> pageResult = routeRepo.findMostPopularRoute(pageable, startDate);
            List<Object[]> allResults = pageResult.getContent();
            // Thêm field mới cho route (field này là chỉ định xếp hạng của route)
            List<Object[]> modifiedResults = new ArrayList<>();
            for (int i = 0; i < allResults.size(); i++) {
                Object[] row = allResults.get(i);
                // Tạo một array mới với một field bổ sung cho chỉ mục
                Object[] newRow = Arrays.copyOf(row, row.length + 1);
                // Thêm giá trị chỉ mục vào field mới
                newRow[row.length] = i + 1; // Chỉ mục của hàng hiện tại trong allResults
                // Thêm vào danh sách kết quả đã sửa
                modifiedResults.add(newRow);
            }

            // Cắt danh sách theo `offset` và `limit`
            List<Object[]> results = modifiedResults.subList(offset, Math.min(offset + limit, modifiedResults.size()));

            Map<String, Object> response = new HashMap<>();
            response.put("data",results); // Dữ liệu của trang hiện tại
            response.put("totalElements", pageResult.getTotalElements()); // Tổng số bản ghi
            // Trả về kết quả hoặc null nếu không có gì
            return response.isEmpty() ? null : response;

        } catch (Exception e) {
            // Log the exception (if logging is set up)
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
            // Return null or handle the error based on the requirement
            return null;
        }
    }
}
