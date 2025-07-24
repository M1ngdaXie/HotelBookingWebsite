package com.example.HotelBooking.controllers;

import com.example.HotelBooking.dtos.ResponseDTO;
import com.example.HotelBooking.dtos.RoomDTO;
import com.example.HotelBooking.entities.RoomType;
import com.example.HotelBooking.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> addRoom(
            @RequestParam Integer roomNumber,
            @RequestParam RoomType roomType,
            @RequestParam BigDecimal pricePerNight,
            @RequestParam Integer capacity,
            @RequestParam String description,
            @RequestParam MultipartFile imageFile
            ){
        RoomDTO roomDTO = RoomDTO.builder()
                .roomNumber(roomNumber)
                .roomType(roomType)
                .pricePerNight(pricePerNight)
                .capacity(capacity)
                .description(description)
                .build();
        return ResponseEntity.ok(roomService.addRoom(roomDTO, imageFile));
    }
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> updateRoom(
            @RequestParam(value = "roomNumber", required = false) Integer roomNumber,
            @RequestParam( value = "roomType", required = false) RoomType roomType,
            @RequestParam( value = "pricePerNight", required = false) BigDecimal pricePerNight,
            @RequestParam( value = "capacity", required = false) Integer capacity,
            @RequestParam( value = "description", required = false) String description,
            @RequestParam( value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "id", required = true) Long id
    ){
        RoomDTO roomDTO = RoomDTO.builder()
                .id(id)
                .roomNumber(roomNumber)
                .roomType(roomType)
                .pricePerNight(pricePerNight)
                .capacity(capacity)
                .description(description)
                .build();
        return ResponseEntity.ok(roomService.updateRoom(roomDTO, imageFile));
    }
    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }
    @DeleteMapping("/delete/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.deleteRoom(roomId));
    }
    @GetMapping("/available")
    public ResponseEntity<ResponseDTO> getAvailableRooms(
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate,
            @RequestParam(required = false) RoomType roomType
    ) {
        System.out.println("roomType param: " + roomType);
        return ResponseEntity.ok(roomService.getAvailableRooms(checkInDate, checkOutDate, roomType));
    }
    @GetMapping("/types")
    public ResponseEntity<List<RoomType>> getAllRoomTypes() {
        return ResponseEntity.ok(roomService.getAllRoomTypes());
    }
    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchRoom(@RequestParam String input) {
        return ResponseEntity.ok(roomService.searchRoom(input));
    }
}

