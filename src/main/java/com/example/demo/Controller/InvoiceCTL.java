package com.example.demo.Controller;

import com.example.demo.Service.InvoiceSV; // Import service RouteSV
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Map<String, Object>> lookupInvoiceByPhone(
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String[] status,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
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
                    statusList.add("1");
                    statusList.add("2");
                    statusList.add("3");
                }
            }
        }
        // Tính toán offset từ pageNum và limit
        int offset = (pageNum - 1) * limit;
        Map<String, Object> invoices = invoiceSV.findInvoicesByPhoneNumber(phoneNumber, statusList, offset, limit);

        // Trả về danh sách hóa đơn
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/lookup-past-bookings")
    public ResponseEntity<Map<String, Object>> lookupPastBookings(
            @RequestParam("customerId") int customerId,  // Nhận customerId từ query parameters
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, // Sử dụng @RequestParam để nhận pageNum
            @RequestParam(value = "limit", defaultValue = "10") int limit) { // Sử dụng @RequestParam để nhận limit

        // Logic phân trang: tính toán offset dựa trên pageNum và limit
        int offset = (pageNum - 1) * limit;

        // Gọi service để tìm các booking quá khứ của customerId với phân trang
        Map<String, Object> pastBookings = invoiceSV.findPastBookingsByCustomerId(customerId, offset, limit);

        // Trả về danh sách các booking quá khứ
        return ResponseEntity.ok(pastBookings);
    }

    @PostMapping("/cancel-ticket/{ticketId}")
    public boolean cancelTicket(@PathVariable("ticketId") int ticketId) {
        try {
            boolean isCancelled = invoiceSV.cancelTicket(ticketId);
            if(isCancelled==true){
                return true;
            }
            else {
                return false;
            }
            // Trả về ResponseEntity với giá trị boolean và mã trạng thái
        } catch (Exception e) {
            // Nếu có lỗi xảy ra, trả về mã trạng thái 500 Internal Server Error
            return false;
        }
    }

    @GetMapping("/staff-lookup-invoice/{idBooking}")
    public ResponseEntity<Object[]> lookupInvoiceByBookingId(
            @PathVariable int idBooking) { // Sử dụng @PathVariable để nhận mã booking

        // Gọi service để tìm hóa đơn dựa trên mã booking
        Object[] invoice = invoiceSV.findInvoiceByBookingId(idBooking); // Chỉ lấy một hóa đơn

        if (invoice == null) {
            return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy hóa đơn
        }

        return ResponseEntity.ok(invoice); // Trả về hóa đơn
    }

    @PutMapping("/update-ticket-status/{idBooking}")
    public boolean updateTicketStatus(
            @PathVariable int idBooking,
            @RequestBody Map<String, String> requestBody) { // Nhận request body dưới dạng Map

        int status = Integer.parseInt(requestBody.get("status").toString()); // Lấy trạng thái từ request body và chuyển đổi thành int

        // Gọi service để cập nhật trạng thái vé
        boolean isUpdated = invoiceSV.updateTicketStatus(idBooking, status);

        if (isUpdated) {
            return true; // Trả về 200 nếu cập nhật thành công
        } else {
            return false; // Trả về 404 nếu không tìm thấy vé
        }
    }
}
