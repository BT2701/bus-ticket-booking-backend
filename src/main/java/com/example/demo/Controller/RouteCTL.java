package com.example.demo.Controller;
import com.example.demo.Model.Route;
import com.example.demo.Service.RouteSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class RouteCTL {
    @Autowired
    private RouteSV routeSV;

    @GetMapping("/api/most-popular-route/{numLimit}")
    public List<Object[]> getMostPopularRoute(@PathVariable("numLimit") int numLimit) {
        return routeSV.getMostPopularRoute(numLimit);
    }

    @GetMapping("api/route-management")
    public List<Route> getRouteManagement(@RequestParam int page, @RequestParam int size) {
        return routeSV.getRouteLimit(page, size);
    }
    @GetMapping("api/route/total")
    public Integer getTotalRoute() {
        return routeSV.getTotalRoute();
    }
}
