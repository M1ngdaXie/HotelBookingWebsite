package com.example.HotelBooking.exceptions;

public class InValidCredentialException extends RuntimeException{
    public InValidCredentialException(String message) {
        super(message);
    }

    public InValidCredentialException(String message, Throwable cause) {
        super(message, cause);
    }
}
