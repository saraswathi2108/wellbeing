package com.wellbeing.controller;


import com.razorpay.RazorpayException;
import com.wellbeing.dto.CreateOrderRequest;
import com.wellbeing.dto.OrderResponse;
import com.wellbeing.dto.VerifyPaymentRequest;
import com.wellbeing.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> verifyPayment(@RequestBody VerifyPaymentRequest request)
            throws Exception {
        return ResponseEntity.ok(paymentService.verifyPayment(request)
        );
    }
    @PostMapping("/payment-failed")
    public ResponseEntity<String> paymentFailed(
            @RequestParam String razorpayOrderId) {

        paymentService.handlePaymentFailure(
                razorpayOrderId);

        return ResponseEntity.ok(
                "Payment marked as failed");
    }

}
