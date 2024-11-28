package com.example.demo.Controller;

import com.example.demo.Model.Notification;
import com.example.demo.Service.NotificationSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class NotificationCTL {
    @Autowired
    private NotificationSV notificationSV;
    @GetMapping("/notification/{customerId}")
    public List<Notification> getMostPopularRoute(@PathVariable("customerId") int customerId) {
        return notificationSV.findByCustomerId(customerId);
    }

}
