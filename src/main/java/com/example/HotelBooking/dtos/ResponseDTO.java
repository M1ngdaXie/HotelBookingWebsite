package com.example.HotelBooking.dtos;

import com.example.HotelBooking.entities.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {
    private int status;
    private String message;

    private String token;
    private UserRole userRole;
    private boolean isActive;
    private String expirationTime;

    //data output
    private UserDTO user;
    private List<UserDTO> users;
    private BookingDTO booking;
    private List<BookingDTO> bookings;
    private RoomDTO room;
    private List<RoomDTO> rooms;
    private PaymentDTO payment;
    private List<PaymentDTO> payments;

    private NotificationDTO notification;
    private List<NotificationDTO> notifications;
    private final LocalDateTime timeStamp = LocalDateTime.now();
}
