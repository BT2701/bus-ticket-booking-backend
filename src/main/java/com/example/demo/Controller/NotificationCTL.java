package com.example.demo.Controller;

import com.example.demo.Model.Notification;
import com.example.demo.Model.Schedule;
import com.example.demo.Service.NotificationSV;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class NotificationCTL {
    @Autowired
    private NotificationSV notificationSV;
    @GetMapping("/notification/{customerId}")
    public List<Notification> getNotificationByCustomer (@PathVariable("customerId") int customerId) {
        List<Notification> notifications = notificationSV.findByCustomerId(customerId);
        return notifications != null ? notifications : new ArrayList<>();
    }

    @PutMapping("/notification/{notificationId}")
    public ResponseEntity<String> updateNotification(@PathVariable("notificationId") int notificationId) {
        // Set readAt to the current time when the PUT request is made
        boolean isUpdated = notificationSV.updateReadAt(notificationId);

        if (isUpdated) {
            return ResponseEntity.ok("Notification updated successfully");
        } else {
            return ResponseEntity.status(404).body("Notification not found");
        }
    }
}
