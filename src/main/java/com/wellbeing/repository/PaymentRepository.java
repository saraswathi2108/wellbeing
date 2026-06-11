package com.wellbeing.repository;

import com.wellbeing.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,String> {
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

     Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}