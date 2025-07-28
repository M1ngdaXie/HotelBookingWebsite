package com.example.HotelBooking.payments.stripe;

import com.example.HotelBooking.dtos.NotificationDTO;
import com.example.HotelBooking.entities.*;
import com.example.HotelBooking.exceptions.NotFoundException;
import com.example.HotelBooking.payments.stripe.dto.PaymentRequest;
import com.example.HotelBooking.repositories.BookingRepository;
import com.example.HotelBooking.repositories.PaymentRepository;
import com.example.HotelBooking.services.NotificationService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Value("${stripe.api.secret.key}")
    private String secretKey;
    public String createPaymentIntent(PaymentRequest paymentRequest) {
        log.info("Inside createPaymentIntent method with booking reference: {}", paymentRequest.getBookingReference());
        Stripe.apiKey = secretKey;
        String bookingReference = paymentRequest.getBookingReference();
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with reference: " + bookingReference));
        if(booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new NotFoundException("You already paid for this booking ! ! !");
        }
        try{
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentRequest.getAmount().multiply(new BigDecimal(100)).longValue()) // Convert to cents
                    .setCurrency("usd")
                    .putMetadata("Payment for booking: ", bookingReference)
                    .build();
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            return paymentIntent.getClientSecret();
        }catch (Exception e){
            log.error("Error creating payment intent for booking: {}", bookingReference, e);
            throw new RuntimeException("Failed to create payment intent");
        }
    }
    public void updatePaymentBooking(PaymentRequest paymentRequest){
        String bookingReference = paymentRequest.getBookingReference();
        System.out.println("=== updatePaymentBooking called ===");
        System.out.println("Booking reference: " + bookingReference);

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking not found with reference: " + bookingReference));
        System.out.println("Fetched booking: " + booking);

        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentGateway(PaymentGateway.STRIPE);
        payment.setAmount(paymentRequest.getAmount());
        payment.setTransactionId(paymentRequest.getTransactionId());
        payment.setPaymentStatus(paymentRequest.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setBookingReference(bookingReference);
        payment.setUser(booking.getUser());
        if(!paymentRequest.isSuccess()) {
            payment.setFailureReason(paymentRequest.getFailureReason());
            System.out.println("Setting failureReason: " + paymentRequest.getFailureReason());
        }
        paymentRepository.save(payment);
        System.out.println("Saved new PaymentEntity: " + payment);

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(booking.getUser().getEmail())
                .type(NotificationType.EMAIL)
                .bookingReference(bookingReference)
                .build();

        if(paymentRequest.isSuccess()){
            System.out.println("Payment marked as SUCCESS: updating booking status to COMPLETED");
            booking.setPaymentStatus(PaymentStatus.COMPLETED);
            bookingRepository.save(booking);
            notificationDTO.setSubject("Payment Successful");
            notificationDTO.setBody("Your payment for booking " + bookingReference + " was successful.");
            System.out.println("Sending success email to: " + booking.getUser().getEmail());
            notificationService.sendEmail(notificationDTO);
        } else {
            System.out.println("Payment marked as FAILED: updating booking status to FAILED");
            booking.setPaymentStatus(PaymentStatus.FAILED);
            bookingRepository.save(booking);
            notificationDTO.setSubject("Payment Failed");
            notificationDTO.setBody("Your payment for booking " + bookingReference + " failed. Reason: " + paymentRequest.getFailureReason());
            System.out.println("Sending failure email to: " + booking.getUser().getEmail());
            notificationService.sendEmail(notificationDTO);
        }

        System.out.println("=== updatePaymentBooking finished ===");
    }
}
