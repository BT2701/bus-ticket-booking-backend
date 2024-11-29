package com.example.demo.Service;

import com.example.demo.DTO.BookingsToNotifyDTO;
import com.example.demo.Model.Booking;
import com.example.demo.Model.Customer;
import com.example.demo.Model.Notification;
import com.example.demo.Model.Schedule;
import com.example.demo.Repository.BookingRepo;
import com.example.demo.Repository.NotificationRepo;
import com.example.demo.Repository.ScheduleRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationSV {
    @Autowired
    private NotificationRepo notificationRepo;
    @Autowired
    private ScheduleRepo scheduleRepo;
    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private EmailService emailService;
    public List<Notification> findByCustomerId (int customerId){
        LocalDateTime timeMinus30Minutes = LocalDateTime.now().minusMinutes(30);
        return  notificationRepo.findUnreadOrRecentByCustomer(customerId,timeMinus30Minutes);
    }


    public void notiActionEditnDelSchedule(int scheduleId, String title, String message) {
        List<Booking> bookings = bookingRepo.bookingsToNotiChangeSchedule(scheduleId);
        for (Booking booking : bookings) {
            createNotification(title, message, booking.getCustomer());
            emailService.sendEmail(booking.getCustomer().getEmail(), title, message);
        }
    }

    public boolean updateReadAt(int notificationId) {
        Notification notification = notificationRepo.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setReadAt(Timestamp.valueOf(LocalDateTime.now()));
            notificationRepo.save(notification);
            return true; // Successfully updated
        }
        return false; // Notification not found
    }

    //Chạy mỗi phút
    @Scheduled(cron = "0 * * * * *")  // Mỗi phút
    public void checkAddNotiSchedule(){
        // Lấy thời gian hiện tại và cộng thêm 30 phút
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureTime = now.plusMinutes(30);
        // Đưa về đầu phút (giây và nano giây bằng 0)
        LocalDateTime timeStart = futureTime.withSecond(0).withNano(0);
        // Đưa về cuối phút (giây là 59 và nano giây là 999999999)
        LocalDateTime timeEnd = futureTime.plusMinutes(1).withSecond(0).withNano(0).minusNanos(1);
        // Chuyển đổi từ LocalDateTime sang Timestamp
        Timestamp timestampStart = Timestamp.valueOf(timeStart);
        Timestamp timestampEnd = Timestamp.valueOf(timeEnd);
        List<BookingsToNotifyDTO> bookingsToNotifyDTOS = bookingRepo.listBookingToNotify(timestampStart,timestampEnd);
        for (BookingsToNotifyDTO bookingsToNotifyDTO : bookingsToNotifyDTOS) {
            String toAddress = bookingsToNotifyDTO.getBooking().getSchedule().getRoute().getTo().getAddress();
            String addressToGetOnBus = bookingsToNotifyDTO.getBooking().getSchedule().getRoute().getTo().getName();
            if(bookingsToNotifyDTO.getIsPayment() == 0){
                // Tạo thông báo cho mỗi vé
                String message = "Vé xe đi: "+toAddress+" của bạn chưa được thanh toán, tới quầy thanh toán để thực hiện";
                createNotification("Thông báo thanh toán vé xe",message,bookingsToNotifyDTO.getBooking().getCustomer());
                emailService.sendEmail(bookingsToNotifyDTO.getBooking().getCustomer().getEmail(), "Thông báo thanh toán vé xe", message);
            }
            // Tạo thông báo cho mỗi vé
            String message = "Xe đi : "+toAddress+ " sẽ khởi hành sau 30 phút nữa hãy đến "+addressToGetOnBus+" để lên xe";
            // Lưu thông báo vào cơ sở dữ liệu
            createNotification("Thông báo tuyến xe sắp khởi hành",message,bookingsToNotifyDTO.getBooking().getCustomer());
            emailService.sendEmail(bookingsToNotifyDTO.getBooking().getCustomer().getEmail(), "Tuyến xe sắp khởi hành", message);
        }
    }


    private Boolean createNotification(String title, String message, Customer customer){
        LocalDateTime now = LocalDateTime.now();
        Timestamp currentDateFormatTimeStamp = Timestamp.valueOf(now);
        Notification thongBao = new Notification();
        thongBao.setTitle(title);
        thongBao.setMessage(message);
        thongBao.setDate(currentDateFormatTimeStamp);
        thongBao.setCustomer(customer);
        Notification notification = notificationRepo.save(thongBao);
        return notification != null;
    }
}
