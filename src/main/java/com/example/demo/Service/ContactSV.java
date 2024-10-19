package com.example.demo.Service;

import com.example.demo.Model.Contact; // Import model Contact
import com.example.demo.Repository.ContactRepo; // Import repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import @Transactional


import java.sql.Timestamp;
import java.util.List;

@Service
public class ContactSV {

    @Autowired
    private ContactRepo contactRepo;
    @Autowired
    private JavaMailSender mailSender; // Khai báo mailSender

    // Phương thức để lưu thông tin liên hệ
    public void saveContact(Contact contact) {
        contact.setCreate_at(new Timestamp(System.currentTimeMillis())); // Thiết lập thời gian hiện tại
        contactRepo.save(contact); // Lưu vào cơ sở dữ liệu
    }
    // Phương thức để lấy tất cả các liên hệ với điều kiện status = 0 và sắp xếp theo create_at từ xa đến gần
    public List<Contact> getAllContacts() {
        return contactRepo.findContactsByStatus(0);
    }

    // Cập nhật trạng thái và gửi email
    @Transactional // Thêm chú thích này để đảm bảo giao dịch
    public boolean sendMailAndUpdateStatus(String requestId, String resolveTitle, String resolveContent, String email) {
        // Chuyển đổi requestId thành int
        int id = Integer.parseInt(requestId);
        // Cập nhật trạng thái của yêu cầu
        // Kiểm tra trạng thái hiện tại của yêu cầu trước khi cập nhật
        int currentStatus = contactRepo.getContactStatusById(id); // Giả định bạn có phương thức này để lấy trạng thái

        // Nếu trạng thái đã bằng 1, trả về false
        if (currentStatus == 1) {
            return false; // Yêu cầu đã được xử lý, không cần thực hiện gì thêm
        }
        try {
            int check = contactRepo.updateContactStatus(id); // Cập nhật trạng thái yêu cầu
            // Kiểm tra xem yêu cầu đã được cập nhật hay chưa
            if (check > 0) {
                // Nếu yêu cầu tồn tại và trạng thái đã được cập nhật, thì gửi email
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject(resolveTitle);
                message.setText(resolveContent);
                mailSender.send(message); // Gửi mail
                return true; // Trả về true nếu gửi mail thành công
            } else {
                return false; // Trả về false nếu không tìm thấy yêu cầu
            }
        } catch (Exception e) {
            e.printStackTrace(); // In ra lỗi nếu có
            return false; // Trả về false nếu có lỗi trong quá trình xử lý
        }
    }
}
