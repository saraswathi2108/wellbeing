package com.wellbeing.controller;


import com.razorpay.RazorpayException;
import com.wellbeing.dto.CreateOrderRequest;
import com.wellbeing.dto.OrderResponse;
import com.wellbeing.dto.VerifyPaymentRequest;
import com.wellbeing.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;






    @PostMapping("/create-order")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) throws Exception {
        return ResponseEntity.ok(paymentService.createOrder(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(
            @RequestBody VerifyPaymentRequest request) throws Exception {

        paymentService.verifyPayment(request);
        return ResponseEntity.ok("Payment Verified");
    }



}
