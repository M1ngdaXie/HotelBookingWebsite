package com.example.HotelBooking.exceptions;

public class InValidBookingAndDateException extends RuntimeException{
    public InValidBookingAndDateException(String message) {
        super(message);
    }

    public InValidBookingAndDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
