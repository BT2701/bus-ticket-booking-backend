package com.example.demo.Controller;

import com.example.demo.Model.Contact; // Import model Contact
import com.example.demo.Service.ContactSV; // Import service ContactSV
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/contacts")
    public ResponseEntity<Map<String, Object>> getContactsByStatus(
            @RequestParam(defaultValue = "10") int limit,  // Mặc định là 10 nếu không có tham số limit
            @RequestParam(defaultValue = "1") int pageNum // Mặc định là trang 1 nếu không có tham số pageNumber
    ) {
        int status=0;
        Pageable pageable = PageRequest.of(pageNum - 1, limit);  // Trang bắt đầu từ 0 trong Pageable

        Page<Contact> contactPage = contactSV.getAllContacts(status, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("contacts", contactPage.getContent());  // Dữ liệu liên hệ
        response.put("totalItems", contactPage.getTotalElements());  // Tổng số bản ghi
        response.put("totalPages", contactPage.getTotalPages());  // Tổng số trang
        response.put("currentPage", pageNum);  // Trang hiện tại

        return ResponseEntity.ok(response);
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
}
