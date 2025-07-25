package com.example.HotelBooking.controllers;

import com.example.HotelBooking.dtos.BookingDTO;
import com.example.HotelBooking.dtos.ResponseDTO;
import com.example.HotelBooking.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
        return ResponseEntity.ok(bookingService.createBooking(bookingDTO));
    }
    @GetMapping("/{bookingReference}")
    public ResponseEntity<ResponseDTO> findBookingByReferenceNo(@PathVariable String bookingReference) {
        return ResponseEntity.ok(bookingService.findBookingByReferenceNo(bookingReference));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> updateBooking(@RequestBody BookingDTO bookingDTO) {
        return ResponseEntity.ok(bookingService.updateBooking(bookingDTO));
    }
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getMyBookings() {
        return ResponseEntity.ok(bookingService.findBookingByCurrentUser());
    }
    @DeleteMapping("/delete/{bookingId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteBooking(@RequestBody @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.deleteBooking(bookingId));
    }
}
