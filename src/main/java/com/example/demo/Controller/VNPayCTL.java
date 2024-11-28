package com.example.demo.Controller;

import com.example.demo.DTO.BookingDTO;
import com.example.demo.DTO.VnPayDTO;
import com.example.demo.Service.BookingSV;
import com.example.demo.configurations.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class VNPayCTL {

    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private BookingSV bookingSV;

    // Endpoint kiểm tra kết nối
    @GetMapping({"", "/api/vnpay"})
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "VNPay API is working");
        return response;
    }

    // Chuyển hướng người dùng đến cổng thanh toán VNPAY
    @PostMapping("/api/vnpay/submitOrder")
    public Map<String, String> submitOrder(@RequestParam("amount") int orderTotal,
                                           @RequestParam("orderInfo") String orderInfo,
                                           HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(request, orderTotal, orderInfo, baseUrl);

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", vnpayUrl);
        return response;
    }

    // Sau khi hoàn tất thanh toán, VNPAY sẽ chuyển hướng trình duyệt về URL này
    @GetMapping("/api/vnpay/vnpay-payment-return")
    public Map<String, Object> paymentCompleted(HttpServletRequest request) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderInfo);
        response.put("totalPrice", totalPrice);
        response.put("paymentTime", paymentTime);
        response.put("transactionId", transactionId);
        response.put("status", paymentStatus == 1 ? "success" : "fail");
        return response;
    }
    @PostMapping("/api/vnpay/payment")
    public ResponseEntity<String> booking(@Valid @RequestBody VnPayDTO vnPayDTO) {
        bookingSV.addVnpay(vnPayDTO.getEmail(), vnPayDTO.getName(), vnPayDTO.getPhone(), vnPayDTO.getSchedule(), vnPayDTO.getSeats(),vnPayDTO.getMethod(),vnPayDTO.getProvider(),vnPayDTO.getTransactionId());
        return ResponseEntity.ok("Booking started");
    }
}
