package com.example.demo.Controller;
import com.example.demo.Service.RouteSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}
