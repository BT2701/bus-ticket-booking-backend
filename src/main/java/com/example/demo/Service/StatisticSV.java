package com.example.demo.Service;
import com.example.demo.Repository.BookingRepo;
import com.example.demo.Repository.RouteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class StatisticSV {
    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private RouteRepo routeRepo;

    //Các hàm thông kê doanh thu toàn hệ thống
    //Lấy  doanh thu theo X ngày gần nhất
    public List<Object[]> doanhThuTheoNgay(int numberTime) {
        // Tính ngày bắt đầu và kết thúc của 7 ngày gần nhất
        LocalDateTime startDate =LocalDateTime.now().minusDays(numberTime-1).truncatedTo(ChronoUnit.DAYS);
        return  bookingRepo.findDailyRevenuesSinceDate(startDate);
    }
    //Lấy  doanh thu từ ngày X1- X2
    public List<Object[]> doanhThuTuyChon(LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepo.findDailyRevenuesBetweenDates(startDate, endDate);
    }
    //Lấy  doanh thu theo X tháng gần nhất
    public List<Object[]> doanhThuTheoThang(int numberTime) {
        // Lấy ngày giờ hiện tại
        LocalDateTime startDate = LocalDateTime.now().withDayOfMonth(1).minusMonths(numberTime - 1);
        return  bookingRepo.findMonthlyRevenuesSinceDate(startDate);
    }


    //Các hàm thông kê doanh thu theo tuyến xe
    //Lấy doanh thu theo X ngày gần nhất (doanh thu theo tuyến xe)
    public List<Object[]> doanhThuTheoNgayVaTuyenXe(int numberTime){
        // Tính ngày bắt đầu và kết thúc của 7 ngày gần nhất
        LocalDateTime startDate = LocalDateTime.now().minusDays(numberTime-1).truncatedTo(ChronoUnit.DAYS);
        return  bookingRepo.findDailyRevenuesByRouteWithStationsSinceDate(startDate);
    }

    //Lấy  doanh thu từ ngày X1- X2 (doanh thu theo tuyến xe)
    public List<Object[]> doanhThuTuyChonTheoTuyenXe(LocalDateTime startDate, LocalDateTime endDate) {
        System.out.print("this is startDate"+startDate);
        System.out.print("this is endDate"+endDate);
        return bookingRepo.findDailyRevenuesByRouteWithStationsBetweenDates(startDate, endDate);
    }

    //Lấy doanh thu theo X ngày gần nhất (doanh thu theo tuyến xe)
    public List<Object[]> doanhThuTheoThangVaTuyenXe(int numberTime){
        LocalDateTime now = LocalDateTime.now();
        // Tính ngày bắt đầu và kết thúc của 7 ngày gần nhất
        LocalDateTime startDate = now.minus(numberTime, ChronoUnit.MONTHS);
        return  bookingRepo.findMonthlyRevenuesByRouteWithStationsSinceDate(startDate);
    }

    //Hàm thông kê số lượng vé bán của từng tuyến xe (mục đích xếp hạng tuyến xe phổ biến) theo thang
    public List<Object[]> getMostPopularRouteDate1toDate2Admin(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> modifiedResults = new ArrayList<>();
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Object[]> allResults = routeRepo.findMostPopularRouteDate1toDate2Admin(startDate, endDate);
            for (int i = 0; i < allResults.size(); i++) {
                Object[] row = allResults.get(i);
                Object[] newRow = Arrays.copyOf(row, row.length + 1);
                newRow[row.length] = i + 1; // The index of the current row in allResults
                modifiedResults.add(newRow);
            }

        } catch (Exception e) {
            System.err.println("An error occurred while processing the route data: " + e.getMessage());
            return null;
        }
        return modifiedResults.isEmpty() ? null : modifiedResults;
    }


    //Lấy trình trạng vé
    public List<Object[]> tinhTrangVeSV() {
        return bookingRepo.tinhTrangVe();
    }
}
