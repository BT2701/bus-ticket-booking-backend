package com.example.demo.Service;

import com.example.demo.Repository.RouteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RouteSV {
    @Autowired
    private RouteRepo routeRepo;

    public List<Object[]> getMostPopularRoute() {
        List<Object[]> results = routeRepo.findMostPopularRoute();
        return results.isEmpty() ? null : results; // Return the top result or null if not found
    }
}
