package com.example.HotelBooking.services;

import com.example.HotelBooking.dtos.BookingDTO;
import com.example.HotelBooking.dtos.ResponseDTO;

public interface BookingService {
    ResponseDTO getAllBookings();
    ResponseDTO createBooking(BookingDTO bookingDTO);
    ResponseDTO findBookingByReferenceNo(String bookingReference);

    ResponseDTO updateBooking(BookingDTO bookingDTO);
    ResponseDTO findBookingByCurrentUser();
    ResponseDTO deleteBooking(Long bookingId);
}
