package com.example.HotelBooking.services;

import com.example.HotelBooking.dtos.ResponseDTO;
import com.example.HotelBooking.dtos.RoomDTO;
import com.example.HotelBooking.entities.RoomType;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    ResponseDTO addRoom(RoomDTO roomDTO, MultipartFile imageFile);
    ResponseDTO updateRoom(RoomDTO roomDTO, MultipartFile imageFile);
    ResponseDTO getAllRooms();
    ResponseDTO getRoomById(Long roomId);

    ResponseDTO getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType);

    List<RoomType> getAllRoomTypes();

    ResponseDTO searchRoom(String input);
    ResponseDTO deleteRoom(Long roomId);
}
