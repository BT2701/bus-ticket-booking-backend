package com.example.demo.Controller;

<<<<<<< HEAD
public class FeedbackCTL {
=======
import com.example.demo.Model.Feedback;
import com.example.demo.Service.FeedbackSV; // Import service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api")
public class FeedbackCTL {

    @Autowired
    private FeedbackSV feedbackSV;

    @GetMapping("/feedback/{scheduleId}")
    public List<Object[]> getFeedback(
            @PathVariable int scheduleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Integer rating) {
        System.out.println("rating"+rating  );
        return feedbackSV.getFeedbackByScheduleId(scheduleId, page, size, rating); // Gọi phương thức từ service và truyền rating
    }

    @GetMapping("/feedback/average/{scheduleId}")
    public List<Object[]> getAverageRating(@PathVariable int scheduleId) {
        System.out.println("scheduleid" + scheduleId);
        return feedbackSV.getFeedbackSummary(scheduleId); // Gọi phương thức từ service
    }

    // Phương thức đếm tổng số phản hồi theo scheduleId và rating
    @GetMapping("/feedback/count/{scheduleId}")
    public long countFeedback(
            @PathVariable int scheduleId,
            @RequestParam int rating) {
        System.out.println("get total" + rating);

        // Kiểm tra nếu rating = 0
        if (rating == 0) {
            // Gọi phương thức getFeedbackSummary để lấy tổng số phản hồi
            List<Object[]> feedbackSummary = feedbackSV.getFeedbackSummary(scheduleId);
            if (!feedbackSummary.isEmpty()) {
                // Lấy giá trị đầu tiên của phần tử đầu tiên
                return (long) feedbackSummary.get(0)[0]; // Trả về giá trị đầu tiên trong phần tử đầu tiên
            } else {
                return 0; // Hoặc bất kỳ giá trị nào bạn muốn trả về nếu không có phản hồi
            }
        } else {
            // Nếu rating khác 0, gọi phương thức đếm phản hồi theo scheduleId và rating
            return feedbackSV.countFeedbackByScheduleIdAndRating(scheduleId, rating);
        }
    }

    // Phương thức thêm feedback
    @PostMapping("/addfeedback")
    public Feedback addFeedback(@RequestBody Feedback feedback) {
        return feedbackSV.addFeedback(feedback); // Gọi phương thức từ service để thêm feedback
    }


>>>>>>> 7ff0c7e0d03a5a208946ea65d332d59e1aa20939
}
