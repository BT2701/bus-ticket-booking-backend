package com.example.demo.Service;

import com.example.demo.Repository.InvoiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceSV {

    // Giả sử bạn có một repository để tìm hóa đơn trong cơ sở dữ liệu
    @Autowired
    private InvoiceRepo invoiceRepository;

    // Hàm tìm hóa đơn dựa trên số điện thoại và status
    public List<Object[]> findInvoicesByPhoneNumber(String phoneNumber, List<String> statusList) {
        // Gọi repository để tìm hóa đơn theo số điện thoại và status
        return invoiceRepository.findInvoiceByPhoneAndStatus(phoneNumber, statusList);
    }


}
