package com.example.demo.Controller;
import com.example.demo.DTO.RoutePopularDTO;
import com.example.demo.Model.Schedule;
import com.example.demo.Service.RouteSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.print.attribute.standard.JobKOctets;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class RouteCTL {
    @Autowired
    private RouteSV routeSV;
    @GetMapping("/api/routes/popular/{currentPage}/{numberRouteRequire}")
    public ResponseEntity<Map<String, Object>> getMostPopularRoute(@PathVariable("numberRouteRequire") int numberRouteRequire, @PathVariable("currentPage") int currentPage) {
        // Lấy dữ liệu từ hàm service
        Map<String, Object> routePopularDTOS = routeSV.getMostPopularRoute(currentPage,numberRouteRequire);
        // Chuyển đổi List<Object[]> thành List<RoutePopularDTO>
        //Mục đích để lên trên frontend dữ liệu sẽ có dạng key-value (với key là tên các thuộc tính mô tả rõ ràng)
        // Convert List<Object[]> to List<RoutePopularDTO>
        @SuppressWarnings("unchecked")
        List<Object[]> dataList = (List<Object[]>) routePopularDTOS.get("data");
        List<RoutePopularDTO> dtoList = dataList.stream()
                .map(objects -> {
                    RoutePopularDTO dto = new RoutePopularDTO();
                    dto.setRouteId((Integer) objects[0]);      // r.id
                    dto.setDistance((Integer) objects[1]);     // r.distance
                    dto.setDuration((Time) objects[2]);        // r.duration
                    dto.setSchedule((Schedule) objects[3]);    // sch.price
                    dto.setFromAddress((String) objects[4]);   // sFrom.address
                    dto.setToAddress((String) objects[5]);     // sTo.address
                    dto.setFromName((String) objects[6]);      // sFrom.name
                    dto.setToName((String) objects[7]);        // sTo.name
                    dto.setQuantityTicket((Long) objects[8]);  // quantityTicket
                    dto.setRank((Integer) objects[9]);         // rank
                    return dto;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", dtoList);
        response.put("totalElements", routePopularDTOS.get("totalElements"));

        // Return ResponseEntity with the map
        return ResponseEntity.ok(response);
    }
}
