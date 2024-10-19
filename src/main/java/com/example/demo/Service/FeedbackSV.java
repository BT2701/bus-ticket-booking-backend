package com.example.demo.Service;

import com.example.demo.Model.Feedback;
import com.example.demo.Repository.FeedbackRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackSV {

    @Autowired
    private FeedbackRepo feedbackRepo;

    public List<Object[]> getFeedbackByScheduleId(int scheduleId, int page, int size, Integer rating) {
        // Tạo đối tượng Pageable để xác định số trang và kích thước trang
        Pageable pageable = PageRequest.of(page, size);

        // Kiểm tra nếu rating khác null, áp dụng bộ lọc theo số sao
        Page<Object[]> feedbackPage;
        if (rating != null) {
            feedbackPage = feedbackRepo.findFeedbackByScheduleIdAndRating(scheduleId, rating, pageable);
        } else {
            feedbackPage = feedbackRepo.findFeedbackByScheduleId(scheduleId, pageable);
        }

        return feedbackPage.getContent(); // Trả về nội dung của trang hiện tại
    }
    public List<Object[]> getFeedbackSummary(int scheduleId) {
        return feedbackRepo.findFeedbackSummaryByScheduleId(scheduleId);
    }

    // Phương thức đếm tổng số phản hồi theo scheduleId và rating
    public long countFeedbackByScheduleIdAndRating(int scheduleId, int rating) {
        return feedbackRepo.countFeedbackByScheduleIdAndRating(scheduleId, rating);
    }

    // Phương thức thêm phản hồi
    public Feedback addFeedback(Feedback feedback) {
        return feedbackRepo.save(feedback);
    }



}
