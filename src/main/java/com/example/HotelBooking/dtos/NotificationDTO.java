package com.example.HotelBooking.dtos;


import com.example.HotelBooking.entities.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// This class represents a Notification entity in the Hotel Booking application.
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDTO {
    private Long Id;

    @NotBlank(message = "Subject cannot be blank")
    private String subject;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    @NotBlank(message = "Body cannot be blank")
    private String body;
    private String bookingReference;

    private NotificationType notificationType;

    private LocalDateTime createdAt = LocalDateTime.now();
}
