package com.example.demo.Repository;

import com.example.demo.Model.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepo extends JpaRepository<Feedback, Integer> {

    @Query("SELECT f.content, f.rating, f.date, c.name " +
            "FROM feedback f " + // Lưu ý: Cần sử dụng tên lớp (Feedback) chứ không phải tên bảng (feedback)
            "JOIN f.booking b " + // Join với bảng Booking thông qua feedback
            "JOIN b.customer c " + // Join với bảng Customer thông qua Booking để lấy tên khách hàng
            "JOIN b.schedule s " + // Join với bảng Schedule thông qua booking
            "WHERE s.id = :scheduleId") // Điều kiện lấy feedback theo scheduleId
    Page<Object[]> findFeedbackByScheduleId(@Param("scheduleId") int scheduleId, Pageable pageable);

    @Query("SELECT COUNT(f), AVG(f.rating) " +
            "FROM feedback f " +
            "JOIN f.booking b " +
            "JOIN b.schedule s " +
            "WHERE s.id = :scheduleId")
    List<Object[]> findFeedbackSummaryByScheduleId(@Param("scheduleId") int scheduleId);

    // Phương thức tìm phản hồi theo scheduleId và rating
    @Query("SELECT f.content, f.rating, f.date, c.name " +
            "FROM feedback f " + // Lưu ý: Cần sử dụng tên lớp (Feedback) chứ không phải tên bảng (feedback)
            "JOIN f.booking b " + // Join với bảng Booking thông qua feedback
            "JOIN b.customer c " + // Join với bảng Customer thông qua Booking để lấy tên khách hàng
            "JOIN b.schedule s " + // Join với bảng Schedule thông qua booking
            "WHERE s.id = :scheduleId AND f.rating = :rating")
    Page<Object[]> findFeedbackByScheduleIdAndRating(@Param("scheduleId") int scheduleId, @Param("rating") int rating, Pageable pageable);

    // Phương thức đếm tổng số phản hồi theo scheduleId và rating
    @Query("SELECT COUNT(f) " +
            "FROM feedback f " + // Lưu ý: Sử dụng tên lớp (Feedback) chứ không phải tên bảng (feedback)
            "JOIN f.booking b " + // Join với bảng Booking thông qua feedback
            "JOIN b.schedule s " + // Join với bảng Schedule thông qua booking
            "WHERE s.id = :scheduleId AND f.rating = :rating")
    long countFeedbackByScheduleIdAndRating(@Param("scheduleId") int scheduleId, @Param("rating") int rating);

}
