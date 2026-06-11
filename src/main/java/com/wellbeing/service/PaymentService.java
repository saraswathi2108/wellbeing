package com.wellbeing.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.wellbeing.config.SecurityUtil;
import com.wellbeing.dto.CreateOrderRequest;
import com.wellbeing.dto.OrderResponse;
import com.wellbeing.dto.VerifyPaymentRequest;
import com.wellbeing.entity.*;
import com.wellbeing.repository.PaymentRepository;
import com.wellbeing.repository.SubscriptionRepository;
import com.wellbeing.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final PaymentRepository paymentRepository;

    @Value("${razor.key.id}")
    private String keyId;

    @Value("${razor.key.secret}")
    private String keySecret;

    public OrderResponse createOrder(CreateOrderRequest request) throws Exception {
        String userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() ->
                        new RuntimeException("User not authenticated"));

        Subscription subscription = subscriptionRepository.findById(request.getSubId())
                        .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (!Boolean.TRUE.equals(subscription.getStatus())) {
            throw new RuntimeException("Subscription is inactive");
        }
        UserSubscription userSubscription = userSubscriptionRepository.findByUserId(userId)
                        .orElseThrow(() -> new RuntimeException("User subscription not found"));

        RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

        JSONObject options = new JSONObject();

        options.put("amount", subscription.getPrice() * 100);
        options.put("currency", "INR");
        options.put("receipt", "USER_" + userId + "_" + System.currentTimeMillis());

        Order order = razorpay.orders.create(options);

        Long paymentCount = paymentRepository.count() + 1;

        String paymentId = String.format("PAY%05d", paymentCount);

        Payment payment = new Payment();

        payment.setPaymentId(paymentId);

        payment.setRazorpayOrderId(order.get("id").toString());

        payment.setAmount(subscription.getPrice());

        payment.setPaymentStatus(PaymentStatus.PENDING);

        payment.setPaymentDate(LocalDateTime.now());

        payment.setUserSubscription(userSubscription);

        paymentRepository.save(payment);

        OrderResponse response = new OrderResponse();

        response.setRazorPayOrderId(order.get("id").toString());

        response.setAmount(subscription.getPrice());

        response.setKeyId(keyId);

        return response;
    }

    public String verifyPayment(VerifyPaymentRequest request) throws Exception {

        String userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        if (paymentRepository.findByRazorpayPaymentId(request.getRazorpayPaymentId()).isPresent()) {
            throw new RuntimeException("Payment already verified");
        }

        boolean validSignature = Utils.verifySignature(
                        request.getRazorpayOrderId()
                                + "|"
                                + request.getRazorpayPaymentId(),
                        request.getRazorpaySignature(),
                        keySecret);

        if (!validSignature) {
            throw new RuntimeException(
                    "Invalid payment signature");
        }

        Subscription subscription = subscriptionRepository.findById(request.getSubId())
                        .orElseThrow(() -> new RuntimeException("Subscription not found"));

        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                        .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());

        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setPaymentMethod("RAZORPAY");
        payment.setPaymentStatus(PaymentStatus.SUCCESSFUL);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        UserSubscription userSubscription = userSubscriptionRepository.findByUserId(userId)
                        .orElseThrow(() -> new RuntimeException("User subscription not found"));

        userSubscription.setSubscription(subscription);

        userSubscription.setStatus(UserSubscriptionStatus.ACTIVE);

        userSubscription.setStartDate(LocalDate.now());

        userSubscription.setEndDate(LocalDate.now().plusDays(subscription.getDurationDays()));

        userSubscriptionRepository.save(userSubscription);

        return "Subscription activated successfully";
    }

    public void handlePaymentFailure(String razorpayOrderId) {

        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                        .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getPaymentStatus() == PaymentStatus.PENDING) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }
    }
}