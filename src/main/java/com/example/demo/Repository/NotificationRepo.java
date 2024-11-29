package com.example.demo.Repository;

import com.example.demo.Model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Integer> {
    @Query("SELECT n FROM notifications n WHERE n.customer.id = :customerId AND (n.readAt IS NULL OR :timeMinus30Minutes <= n.readAt) ORDER BY n.readAt ASC")
    List<Notification> findUnreadOrRecentByCustomer(@Param("customerId") int customerId, @Param("timeMinus30Minutes") LocalDateTime timeMinus30Minutes);
}
