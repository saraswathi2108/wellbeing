package com.wellbeing.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wellbeing.entity.ConsultationBooking;
import com.wellbeing.entity.PaymentStatus;

@Repository
public interface ConsultationBookingRepo extends JpaRepository<ConsultationBooking, Long> {

	List<ConsultationBooking> findByPaymentStatusOrderByCreatedAtDesc(PaymentStatus successful, Pageable pageable);

}
