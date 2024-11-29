package com.example.demo.Controller;
import com.example.demo.DTO.RoutePopularDTO;
import com.example.demo.Model.Schedule;
import com.example.demo.Service.StatisticSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class StatisticCTL {
    @Autowired
    private StatisticSV statisticSV;

    //numberTime là số lượng ngày mà use họ truyền xuống, và nằm trong 3 giá trị (7,14,30)
    @GetMapping("/api/statistic/doanhthuhethong/doanhthutheongay/{numberTime}")
    public List<Object[]> doanhThuTheoNgay(@PathVariable int numberTime) {
        return statisticSV.doanhThuTheoNgay(numberTime);
    }

    //Doanh thu tùy chọn có nghĩa là người dùng chọn ngày bắt đầu và ngày kết thúc (giới hạn trong 30 ngày)
    @GetMapping("/api/statistic/doanhthuhethong/doanhthutuychon/{timeStart}/{timeEnd}")
    public List<Object[]> doanhThuTuyChon(@PathVariable String timeStart, @PathVariable String timeEnd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDate = LocalDate.parse(timeStart, formatter).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(timeEnd, formatter).atTime(23, 59, 59); // Đặt endDate là cuối ngày
        return statisticSV.doanhThuTuyChon(startDate, endDate);
    }
    //Doanh thu theo tháng
    @GetMapping("/api/statistic/doanhthuhethong/doanhthutheothang/{numberTime}")
    public List<Object[]> doanhThuTheoThang(@PathVariable int numberTime)
    {
        return statisticSV.doanhThuTheoThang(numberTime);
    }

    //Phần doanh thu theo tuyến xe
    //1: Doanh thu theo ngày và tuyến xe
    @GetMapping("/api/statistic/doanhthutheotuyen/doanhthutheongay/{numberTime}")
    public List<Object[]> doanhThuTheoNgayVaTuyenXe(@PathVariable int numberTime)
    {
        return statisticSV.doanhThuTheoNgayVaTuyenXe(numberTime);
    }
    //2: Doanh thu theo tháng và tuyến xe
    @GetMapping("/api/statistic/doanhthutheotuyen/doanhthutheothang/{numberTime}")
    public List<Object[]> doanhThuTheoThangVaTuyenXe(@PathVariable int numberTime)
    {
        return statisticSV.doanhThuTheoThangVaTuyenXe(numberTime);
    }

    //3.Doanh thu tùy chọn có nghĩa là người dùng chọn ngày bắt đầu và ngày kết thúc (giới hạn trong 30 ngày)
    @GetMapping("/api/statistic/doanhthutheotuyen/doanhthutuychon/{timeStart}/{timeEnd}")
    public List<Object[]> doanhThuTuyChonTheoTuyenXe(@PathVariable String timeStart, @PathVariable String timeEnd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDate = LocalDate.parse(timeStart, formatter).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(timeEnd, formatter).atTime(23, 59, 59); // Đặt endDate là cuối ngày
        return statisticSV.doanhThuTuyChonTheoTuyenXe(startDate, endDate);
    }


    //Phần tuyến xe phổ biến
    @GetMapping("/api/statistic/tuyenxephobien/{timeStart}/{timeEnd}")
    public List<RoutePopularDTO> tuyenXePhoBienXtoX(@PathVariable String timeStart, @PathVariable String timeEnd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDate = LocalDate.parse(timeStart, formatter).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(timeEnd, formatter).atTime(23, 59, 59); // Đặt endDate là cuối ngày
        System.out.print("dddd"+startDate);
        List<Object[]> routePopularDTOS =  statisticSV.getMostPopularRouteDate1toDate2Admin(startDate,endDate);
        //check xemm routePopularDTOS co null hay khong? nếu trả về giá trị null sẽ bị lỗi 401
        if (routePopularDTOS == null || routePopularDTOS.isEmpty()) {
            return Collections.emptyList();  // Trả về danh sách rỗng
        }
        return routePopularDTOS.stream()
                .map(objects -> {
                    // Ánh xạ từng phần tử trong Object[] vào RoutePopularDTO
                    RoutePopularDTO dto = new RoutePopularDTO();
                    dto.setRouteId((Integer) objects[0]);       // r.id// r.id
                    dto.setDistance((Integer) objects[1]);      // r.distance
                    dto.setDuration((Time) objects[2]);       // r.duration
                    dto.setSchedule((Schedule) objects[3]);    // sch.price
                    dto.setFromAddress((String) objects[4]);    // sFrom.address
                    dto.setToAddress((String) objects[5]);      // sTo.address
                    dto.setFromName((String) objects[6]);    // sFrom.address
                    dto.setToName((String) objects[7]);      // sTo.address
                    dto.setQuantityTicket((Long) objects[8]); // quantityTicket
                    dto.setRank((Integer) objects[9]); // quantityTicket
                    return dto;
                })
                .collect(Collectors.toList());
    }


    //Lấy tình trạng vé
    @GetMapping("/api/statistic/tinhtrangve")
    public List<Object[]> tinhTrangVe() {
        List<Object[]> result = statisticSV.tinhTrangVeSV();
        return result != null ? result : new ArrayList<>();
    }
}
