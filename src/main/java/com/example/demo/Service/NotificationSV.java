package com.example.demo.Service;

import com.example.demo.DTO.BookingsToNotifyDTO;
import com.example.demo.Model.Booking;
import com.example.demo.Model.Notification;
import com.example.demo.Model.Schedule;
import com.example.demo.Repository.BookingRepo;
import com.example.demo.Repository.NotificationRepo;
import com.example.demo.Repository.ScheduleRepo;
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
    public List<Notification> findByCustomerId (int customerId){
        return  notificationRepo.findByCustomerId(customerId);
    }

    //Chạy mỗi phút
    @Scheduled(cron = "0 * * * * *")  // Mỗi phút
    public void checkAddNotiSchedule(){
        System.out.println("running...");
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
            if(bookingsToNotifyDTO.getIsPayment() == 0){
                // Tạo thông báo cho mỗi vé
                String message = "Tới quầy thanh toán để thanh toán vé đi " + toAddress;
                // Lưu thông báo vào cơ sở dữ liệu
                Timestamp currentDateFormatTimeStamp = Timestamp.valueOf(now);
                Notification thongBao = new Notification();
                thongBao.setMessage(message);
                thongBao.setDate(currentDateFormatTimeStamp);
                thongBao.setStatus(0);
                thongBao.setCustomer(bookingsToNotifyDTO.getBooking().getCustomer());
                notificationRepo.save(thongBao);
            }
            // Tạo thông báo cho mỗi vé
            String message = "Tuyến xe đi "+toAddress+ " khởi hành sau 30 phút nữa hãy đến điểm lên xe được in trên vé";
            // Lưu thông báo vào cơ sở dữ liệu
            Timestamp currentDateFormatTimeStamp = Timestamp.valueOf(now);
            Notification thongBao = new Notification();
            thongBao.setMessage(message);
            thongBao.setDate(currentDateFormatTimeStamp);
            thongBao.setStatus(0);
            thongBao.setCustomer(bookingsToNotifyDTO.getBooking().getCustomer());
            notificationRepo.save(thongBao);
        }
    }

//    //Chạy mỗi phút
//    @Scheduled(cron = "0 * * * * *")  // Mỗi phút
//    public void checkDeleteNoti(){
//        System.out.println("running...");
//        LocalDateTime currentDate = LocalDateTime.now();
//        LocalDateTime currentDatePlus30Minutes = currentDate.minusMinutes(30);
//        List<Booking> bookings = scheduleRepo.lichTrinhChuaChay(currentDate, currentDatePlus30Minutes);
//        for (Booking booking : bookings) {
//            System.out.println("id lich trinh "+ booking.getSchedule().getId());
//            // Tạo thông báo cho mỗi vé
//            String message = "Chuyến xe của bạn sẽ khởi hành lúc " + booking.getSchedule().getDeparture().toLocalDateTime() + " Hãy đến "+booking.getSchedule().getRoute().getFrom().getName() +" để lên xe";
//
//            // Lưu thông báo vào cơ sở dữ liệu
//            Timestamp currentDateFormatTimeStamp = Timestamp.valueOf(currentDate);
//            Notification thongBao = new Notification();
//            thongBao.setMessage(message);
//            thongBao.setDate(currentDateFormatTimeStamp);
//            thongBao.setStatus(0);
//            thongBao.setCustomer(booking.getCustomer());
//            notificationRepo.save(thongBao);
//        }
//    }
}
