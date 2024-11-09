package com.example.demo.Controller;

<<<<<<< HEAD
public class ContactCTL {
=======
import com.example.demo.Model.Contact; // Import model Contact
import com.example.demo.Service.ContactSV; // Import service ContactSV
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api")
public class ContactCTL {

    @Autowired
    private ContactSV contactSV;

    // Phương thức để nhận dữ liệu từ form và lưu vào database
    @PostMapping("/contact")
    public String createContact(@RequestBody Contact contact) {
        contactSV.saveContact(contact);
        return "Liên hệ đã được gửi thành công!";
    }
    // Phương thức để lấy danh sách yêu cầu liên hệ
    @GetMapping("/contacts") // Đường dẫn API
    public List<Contact> getAllContacts() {
        return contactSV.getAllContacts(); // Gọi phương thức từ service
    }

    // Phương thức để gửi mail và cập nhật trạng thái của yêu cầu
    @PostMapping("/send-mail-and-update-status")
    public boolean sendMailAndUpdateStatus(
            @RequestBody Map<String, String> request) {
        String requestId = request.get("requestId");
        String resolveTitle = request.get("resolveTitle");
        String resolveContent = request.get("resolveContent");
        String email = request.get("email");
//        System.out.println("email"+email+" "+requestId+" "+resolveTitle+" "+resolveContent);

        // Gọi service để gửi mail và cập nhật trạng thái yêu cầu
        boolean isSent = contactSV.sendMailAndUpdateStatus(requestId, resolveTitle, resolveContent, email);

        return isSent;
    }
>>>>>>> 7ff0c7e0d03a5a208946ea65d332d59e1aa20939
}
