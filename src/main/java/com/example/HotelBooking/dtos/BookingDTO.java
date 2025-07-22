package com.example.HotelBooking.dtos;

import com.example.HotelBooking.entities.BookingStatus;
import com.example.HotelBooking.entities.PaymentStatus;
import com.example.HotelBooking.entities.Room;
import com.example.HotelBooking.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class BookingDTO {

    private Long Id;


    private UserDTO user;



    private RoomDTO room;
    private Long roomId;

    private PaymentStatus paymentStatus;

    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;

    private BigDecimal totalPrice;
    private String bookingReference;
    private LocalDateTime createdAt;

    private BookingStatus bookingStatus;

}
