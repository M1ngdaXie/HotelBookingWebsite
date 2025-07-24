package com.example.HotelBooking.controllers;

import com.example.HotelBooking.dtos.ResponseDTO;
import com.example.HotelBooking.dtos.UserDTO;
import com.example.HotelBooking.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllUsers() {
        ResponseDTO response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateOwnAccount(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateOwnAccount(userDTO));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDTO> deleteOwnAccount() {
        return ResponseEntity.ok(userService.deleteOwnAccount());
    }

    @GetMapping("/account")
    public ResponseEntity<ResponseDTO> getOwnAccountDetails() {
        return ResponseEntity.ok(userService.getOwnAccountDetails());
    }

    @GetMapping("/bookings")
    public ResponseEntity<ResponseDTO> getMyBookingHistory() {
        return ResponseEntity.ok(userService.getMyBookingHistory());

    }
}
