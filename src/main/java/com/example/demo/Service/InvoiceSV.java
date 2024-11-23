package com.example.demo.Service;

import com.example.demo.Repository.InvoiceRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional; // Import @Transactional
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.Model.Booking; // Import model Contact

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional // Đảm bảo rằng toàn bộ lớp dịch vụ chạy trong một giao dịch
public class InvoiceSV {

    // Giả sử bạn có một repository để tìm hóa đơn trong cơ sở dữ liệu
    @Autowired
    private InvoiceRepo invoiceRepository;

    // Hàm tìm hóa đơn dựa trên số điện thoại và status
    public Map<String, Object> findInvoicesByPhoneNumber(String phoneNumber, List<String> statusList, int offset, int limit) {
        // Tính toán số trang từ offset và limit
        int page = offset / limit;

        // Tạo Pageable với trang và giới hạn
        Pageable pageable = PageRequest.of(page, limit);
        // Gọi repository để lấy dữ liệu phân trang
        Page<Object[]> pageResult = invoiceRepository.findInvoiceByPhoneAndStatus(phoneNumber, statusList, pageable);

        // Chuẩn bị phản hồi trả về với dữ liệu và thông tin phân trang
        Map<String, Object> response = new HashMap<>();
        response.put("data", pageResult.getContent()); // Dữ liệu của trang hiện tại
        response.put("totalElements", pageResult.getTotalElements()); // Tổng số bản ghi
        response.put("currentPage", pageResult.getNumber()); // Số trang hiện tại
        response.put("totalPages", pageResult.getTotalPages()); // Tổng số trang

        return response;
    }

//    public List<Object[]> findPastBookingsByCustomerId(int customerId) {
//        // Gọi repository để tìm các booking theo customerId và status = 2, arrival < ngày hôm nay
//        return invoiceRepository.findBookingsByCustomerIdAndStatusAndArrivalBeforeToday(customerId);
//    }

    // Hàm tìm hóa đơn dựa trên customerId, status = 2 và schedule.arrival < ngày hôm nay
    public Map<String, Object> findPastBookingsByCustomerId(int customerId, int offset, int limit) {
        // Tính toán số trang từ offset và limit
        int page = offset / limit;
        Pageable pageable = PageRequest.of(page, limit);

        // Gọi repository để lấy dữ liệu booking quá khứ của customerId với status = 2 và arrival < ngày hôm nay
        Page<Object[]> pageResult = invoiceRepository.findBookingsByCustomerIdAndStatusAndArrivalBeforeToday(customerId, pageable);

        // Chuẩn bị phản hồi trả về với dữ liệu và thông tin phân trang
        Map<String, Object> response = new HashMap<>();
        response.put("data", pageResult.getContent()); // Dữ liệu booking
        response.put("totalElements", pageResult.getTotalElements()); // Tổng số bản ghi
        response.put("currentPage", pageResult.getNumber()); // Số trang hiện tại
        response.put("totalPages", pageResult.getTotalPages()); // Tổng số trang

        return response;
    }

    // Hàm hủy vé bằng cách cập nhật status
    public boolean cancelTicket(int ticketId) {
        // Lấy trạng thái của vé
        int status = invoiceRepository.findStatusByTicketId(ticketId);

        // Kiểm tra xem vé có tồn tại và có thể hủy hay không
        if (status == 1) { // status = 1 tức là vé chưa in
            System.out.println(("status:"+status));
            // Cập nhật trạng thái vé thành '3' (đã hủy)
            invoiceRepository.updateTicketStatus(ticketId); // Gọi phương thức cập nhật trạng thái vé

            // Nếu không có lỗi xảy ra trong quá trình cập nhật, trả về true
            return true;
        }

        return false; // Nếu không tìm thấy vé hoặc vé không thể hủy
    }

    // Hàm tìm hóa đơn dựa trên danh sách mã booking
    public Object[] findInvoiceByBookingId(int idBooking) {
        return invoiceRepository.findInvoiceByBookingId(idBooking); // Gọi repository để tìm hóa đơn theo mã booking
    }

    public boolean updateTicketStatus(int idBooking, Integer newStatus) {
        // Kiểm tra xem vé có tồn tại hay không
        Integer existingStatus = invoiceRepository.findStatusByTicketId(idBooking);
        if (existingStatus == null) {
            return false; // Vé không tồn tại
        }

        // Cập nhật trạng thái vé
        invoiceRepository.updateTicketStatusByBookingId(idBooking, newStatus);

        return true; // Trả về true nếu cập nhật thành công
    }
}
