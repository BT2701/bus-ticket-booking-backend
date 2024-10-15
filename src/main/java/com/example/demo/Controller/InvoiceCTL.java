package com.example.demo.Controller;

import com.example.demo.Service.InvoiceSV; // Import service RouteSV
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api")
public class InvoiceCTL {

    @Autowired
    private InvoiceSV invoiceSV;

    @GetMapping("/lookup-invoice")
    public ResponseEntity<List<Object[]>> lookupInvoiceByPhone(
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String[] status) { // Thay đổi để nhận mảng status

        // Khởi tạo danh sách để chứa các giá trị status
        List<String> statusList = new ArrayList<>();

        // Kiểm tra và thay đổi giá trị của status
        if (status != null) {
            for (String s : status) {
                if ("unused".equalsIgnoreCase(s)) {
                    statusList.add("1"); // Thay đổi thành '1' nếu status là 'unused'
                } else if ("used".equalsIgnoreCase(s)) {
                    statusList.add("2"); // Thay đổi thành '2' nếu status là 'used'
                } else {
                    statusList.add("1"); // Thay đổi thành '1' nếu status là 'unused'
                    statusList.add("2"); // Thay đổi thành '2' nếu status là 'used'
                }
            }
        }

        // Gọi service để tìm kiếm hóa đơn dựa trên số điện thoại và status
        List<Object[]> invoices = invoiceSV.findInvoicesByPhoneNumber(phoneNumber, statusList); // Truyền cả phoneNumber và status

        // Trả về danh sách hóa đơn
        return ResponseEntity.ok(invoices);
    }


}
