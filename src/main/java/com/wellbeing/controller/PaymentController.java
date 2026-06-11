package com.wellbeing.controller;


import com.razorpay.RazorpayException;
import com.wellbeing.dto.CreateOrderRequest;
import com.wellbeing.dto.OrderResponse;
import com.wellbeing.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

//    @PostMapping("/create-order")
//    public OrderResponse createOrder(
//            @RequestBody CreateOrderRequest request)
//            throws RazorpayException {
//
//        return paymentService.createOrder(
//                request);
//    }
}
