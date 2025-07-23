package com.example.HotelBooking.services.impl;

import com.example.HotelBooking.dtos.NotificationDTO;
import com.example.HotelBooking.entities.Notification;
import com.example.HotelBooking.entities.NotificationType;
import com.example.HotelBooking.repositories.NotificationRepository;
import com.example.HotelBooking.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final JavaMailSender javaMailSender;
    private final NotificationRepository notificationRepository;
    @Override
    @Async
    public void sendEmail(NotificationDTO notificationDTO) {
        log.info("Inside sendEmail ");
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(notificationDTO.getRecipient());
        mailMessage.setSubject(notificationDTO.getSubject());
        mailMessage.setText(notificationDTO.getBody());

        javaMailSender.send(mailMessage);

        Notification notification = Notification.builder()
                .recipient(notificationDTO.getRecipient())
                .subject(notificationDTO.getSubject())
                .body(notificationDTO.getBody())
                .bookingReference(notificationDTO.getBookingReference())
                .notificationType(NotificationType.EMAIL)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public void sendSms() {

    }

    @Override
    public void sendWhatsApp() {

    }
}
