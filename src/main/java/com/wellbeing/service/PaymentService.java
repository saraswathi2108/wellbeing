package com.wellbeing.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.wellbeing.ExceptionHandler.AlreadyExistsException;
import com.wellbeing.ExceptionHandler.BadRequestException;
import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
import com.wellbeing.ExceptionHandler.UnauthorizedException;
import com.wellbeing.config.SecurityUtil;
import com.wellbeing.dto.CreateOrderRequest;
import com.wellbeing.dto.OrderResponse;
import com.wellbeing.dto.VerifyPaymentRequest;
import com.wellbeing.entity.*;
import com.wellbeing.repository.PaymentRepository;
import com.wellbeing.repository.SubscriptionRepository;
import com.wellbeing.repository.UserRepository;
import com.wellbeing.repository.UserSubscriptionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final SubscriptionRepository subscriptionRepository;
	private final UserSubscriptionRepository userSubscriptionRepository;
	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;
	private final RazorpayClient razorpayClient;
	

	@Value("${razor.key.id}")
	private String keyId;

	@Value("${razor.key.secret}")
	private String keySecret;
	
	

	public OrderResponse createOrder(CreateOrderRequest request) throws Exception {

		String userId = SecurityUtil.getCurrentUserId()
				.orElseThrow(() -> new UnauthorizedException("User not authenticated"));

		Users user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		Subscription subscription = subscriptionRepository.findById(request.getSubId())
				.orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

		if (!Boolean.TRUE.equals(subscription.getStatus())) {
			throw new BadRequestException("Subscription is inactive");
		}

		if (Boolean.TRUE.equals(subscription.getTrialPlan())) {
			throw new BadRequestException("Trial plans cannot be purchased");
		}

		JSONObject options = new JSONObject();
		options.put("amount", subscription.getPrice() * 100);
		options.put("currency", "INR");
		options.put("receipt", "USER_" + userId + "_" + System.currentTimeMillis());

		Order order = razorpayClient.orders.create(options);

		Long paymentCount = paymentRepository.count() + 1;
		String paymentId = String.format("PAY%05d", paymentCount);

		Payment payment = new Payment();
		payment.setPaymentId(paymentId);
		payment.setRazorpayOrderId(order.get("id"));
		payment.setAmount(subscription.getPrice());
		payment.setPaymentStatus(PaymentStatus.PENDING);
		payment.setPaymentDate(LocalDateTime.now());

		payment.setUser(user);
		payment.setSubscription(subscription);

		paymentRepository.save(payment);

		OrderResponse response = new OrderResponse();
		response.setRazorPayOrderId(order.get("id").toString());
		response.setAmount(subscription.getPrice());
		response.setKeyId(keyId);

		return response;
	}
	
	
	

	@Transactional
	public String verifyPayment(VerifyPaymentRequest request) throws Exception {

		String userId = SecurityUtil.getCurrentUserId()
				.orElseThrow(() -> new UnauthorizedException("User not authenticated"));

		if (paymentRepository.findByRazorpayPaymentId(request.getRazorpayPaymentId()).isPresent()) {
			throw new AlreadyExistsException("Payment already verified");
		}

		boolean validSignature = Utils.verifySignature(
				request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId(), request.getRazorpaySignature(),
				keySecret);

		if (!validSignature) {
			throw new BadRequestException("Invalid payment signature");
		}

		// SECURITY FIX 1: Frontend icche SubId ni theesey. Manam DB lo createOrder appudu save chesina payment order theesko.
		Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

		// SECURITY FIX 2: Payment nunchi gattiga bind ayyina Subscription details laagu.
		Subscription subscription = payment.getSubscription();
		if (subscription == null) {
			throw new BadRequestException("No subscription linked to this payment order");
		}

		// SECURITY FIX 3: Ee payment create chesina user e verify chesthunnada leda check chey (Cross-user access prevention)
		Users user = payment.getUser();
		if (!user.getId().equals(userId)) {
			throw new UnauthorizedException("This payment order does not belong to you");
		}

		payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
		payment.setRazorpaySignature(request.getRazorpaySignature());
		payment.setPaymentMethod("RAZORPAY");
		payment.setPaymentStatus(PaymentStatus.SUCCESSFUL);
		payment.setPaymentDate(LocalDateTime.now());

		paymentRepository.save(payment);

		// Expire existing active subscriptions
		List<UserSubscription> activeSubscriptions = userSubscriptionRepository.findAllByUser_IdAndStatus(userId,
				UserSubscriptionStatus.ACTIVE);

		for (UserSubscription sub : activeSubscriptions) {
			sub.setStatus(UserSubscriptionStatus.EXPIRED);
		}

		userSubscriptionRepository.saveAll(activeSubscriptions);

		// Create new subscription history record
		Long count = userSubscriptionRepository.count() + 1;
		String userSubId = String.format("US%03d", count);

		UserSubscription newSubscription = new UserSubscription();
		newSubscription.setUserSubId(userSubId);
		newSubscription.setUser(user);
		newSubscription.setSubscription(subscription);
		newSubscription.setStatus(UserSubscriptionStatus.ACTIVE);
		newSubscription.setStartDate(LocalDate.now());
		newSubscription.setEndDate(LocalDate.now().plusDays(subscription.getDurationDays()));

		userSubscriptionRepository.save(newSubscription);

		return "Subscription activated successfully";
	}
	
	
	

	public void handlePaymentFailure(String razorpayOrderId) {

		Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

		if (payment.getPaymentStatus() == PaymentStatus.PENDING) {

			payment.setPaymentStatus(PaymentStatus.FAILED);

			paymentRepository.save(payment);
		}
	}
}
