package com.example.demo.Repository;

import com.example.demo.Model.Ewalletpay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EwalletRepo extends JpaRepository<Ewalletpay, Integer> {
    @Query("select e from ewalletpay e where e.payment.id=:paymentId")
    public Ewalletpay findByPayment(@Param("paymentId") int paymentId);
}
