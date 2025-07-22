package com.example.HotelBooking.repositories;

import com.example.HotelBooking.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Custom query methods can be added here if needed
    // For example, to find notifications by user ID or type
    // List<Notification> findByUserId(Long userId);
    // List<Notification> findByType(NotificationType type);
}
