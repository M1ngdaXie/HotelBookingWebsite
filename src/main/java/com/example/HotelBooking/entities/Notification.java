package com.example.HotelBooking.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// This class represents a Notification entity in the Hotel Booking application.
@Entity
@Data
@Table(name = "notifications")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String subject;
    @NotBlank(message = "recipient is required")
    private String recipient;
    private String body;
    private String bookingReference;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private final LocalDateTime createdAt = LocalDateTime.now();
}
