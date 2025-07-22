package com.example.HotelBooking.exceptions;

public class NameValueRequiredException extends RuntimeException{
    public NameValueRequiredException(String message) {
        super(message);
    }

    public NameValueRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
