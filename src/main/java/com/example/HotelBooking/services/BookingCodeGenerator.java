package com.example.HotelBooking.services;

import com.example.HotelBooking.entities.BookingReference;
import com.example.HotelBooking.repositories.BookingReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class BookingCodeGenerator {
    private final BookingReferenceRepository bookingReferenceRepository;
    public String generateBookingReferenceCode() {
        String reference;
        do {
            reference = generateRandomAlphaNumericCode(10);
        } while (isBookingReferenceExists(reference));

        saveBookingReference(reference);
        return reference;
    }
    private String generateRandomAlphaNumericCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            code.append(characters.charAt(index));
        }
        return code.toString();
    }
    private boolean isBookingReferenceExists(String reference) {
        return bookingReferenceRepository.findByReferenceNo(reference).isPresent();
    }
    private void saveBookingReference(String reference) {
        BookingReference bookingReference = BookingReference.builder().referenceNo(reference).build();

        bookingReferenceRepository.save(bookingReference);
    }

}
