package com.example.HotelBooking.services.impl;

import com.example.HotelBooking.dtos.ResponseDTO;
import com.example.HotelBooking.dtos.RoomDTO;
import com.example.HotelBooking.entities.Room;
import com.example.HotelBooking.entities.RoomType;
import com.example.HotelBooking.exceptions.InValidBookingAndDateException;
import com.example.HotelBooking.repositories.RoomRepository;
import com.example.HotelBooking.services.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private static final String IMAGE_DIRECTORY = System.getProperty("user.dir") + "/product-image/";

    @Override
    public ResponseDTO addRoom(RoomDTO roomDTO, MultipartFile imageFile) {
        try {
            if (roomRepository.existsByRoomNumber(roomDTO.getRoomNumber())) {
                return ResponseDTO.builder()
                        .status(409)
                        .message("A room with number " + roomDTO.getRoomNumber() + " already exists. Please choose a different number.")
                        .build();
            }
            Room roomToSave = modelMapper.map(roomDTO, Room.class);
            if (imageFile != null) {
                String imagePath = saveImage(imageFile);
                roomToSave.setImageUrl(imagePath);
            }
            roomRepository.save(roomToSave);
            return ResponseDTO.builder()
                    .status(200)
                    .message("Room added successfully.")
                    .build();
        } catch (DataIntegrityViolationException e) {
            return ResponseDTO.builder()
                    .status(409)
                    .message("Duplicate entry detected: " + e.getMostSpecificCause().getMessage())
                    .build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .status(500)
                    .message("An unexpected error occurred while adding the room: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseDTO updateRoom(RoomDTO roomDTO, MultipartFile imageFile) {
        try {
            Room existingRoom = roomRepository.findById(roomDTO.getId())
                    .orElse(null);
            if (existingRoom == null) {
                return ResponseDTO.builder()
                        .status(404)
                        .message("Room not found with id: " + roomDTO.getId())
                        .build();
            }
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = saveImage(imageFile);
                existingRoom.setImageUrl(imagePath);
            }
            if (roomDTO.getRoomNumber() != null && roomDTO.getRoomNumber() >= 0) {
                if (roomRepository.existsByRoomNumber(roomDTO.getRoomNumber()) &&
                        !existingRoom.getRoomNumber().equals(roomDTO.getRoomNumber())) {
                    return ResponseDTO.builder()
                            .status(409)
                            .message("A room with number " + roomDTO.getRoomNumber() + " already exists. Please choose a different number.")
                            .build();
                }
                existingRoom.setRoomNumber(roomDTO.getRoomNumber());
            }
            if (roomDTO.getPricePerNight() != null && roomDTO.getPricePerNight().compareTo(BigDecimal.ZERO) >= 0) {
                existingRoom.setPricePerNight(roomDTO.getPricePerNight());
            }
            if (roomDTO.getCapacity() != null && roomDTO.getCapacity() > 0) {
                existingRoom.setCapacity(roomDTO.getCapacity());
            }
            if (roomDTO.getRoomType() != null) {
                existingRoom.setRoomType(roomDTO.getRoomType());
            }
            if (roomDTO.getDescription() != null && !roomDTO.getDescription().isEmpty()) {
                existingRoom.setDescription(roomDTO.getDescription());
            }
            roomRepository.save(existingRoom);

            return ResponseDTO.builder()
                    .status(200)
                    .message("Room updated successfully.")
                    .build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .status(500)
                    .message("An unexpected error occurred while updating the room: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseDTO getAllRooms() {
        try {
            List<Room> rooms = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            if (rooms.isEmpty()) {
                return ResponseDTO.builder()
                        .status(404)
                        .message("No rooms found.")
                        .build();
            }
            return ResponseDTO.builder()
                    .status(200)
                    .message("Rooms retrieved successfully.")
                    .rooms(rooms.stream().map(room -> modelMapper.map(room, RoomDTO.class)).toList())
                    .build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .status(500)
                    .message("An error occurred while retrieving rooms: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseDTO getRoomById(Long roomId) {
        try {
            Room room = roomRepository.findById(roomId)
                    .orElse(null);
            if (room == null) {
                return ResponseDTO.builder()
                        .status(404)
                        .message("Room not found with id: " + roomId)
                        .build();
            }
            RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);
            return ResponseDTO.builder()
                    .status(200)
                    .message("Room retrieved successfully.")
                    .room(roomDTO)
                    .build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .status(500)
                    .message("An error occurred while retrieving the room: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseDTO getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType) {
        try {
            if (checkInDate == null || checkOutDate == null) {
                return ResponseDTO.builder()
                        .status(400)
                        .message("Check-in and check-out dates cannot be null.")
                        .build();
            }
            if (checkInDate.isBefore(LocalDate.now())) {
                return ResponseDTO.builder()
                        .status(400)
                        .message("Check-in date cannot be in the past.")
                        .build();
            }
            if (checkOutDate.isBefore(checkInDate)) {
                return ResponseDTO.builder()
                        .status(400)
                        .message("Check-out date cannot be before check-in date.")
                        .build();
            }
            if (checkInDate.isEqual(checkOutDate)) {
                return ResponseDTO.builder()
                        .status(400)
                        .message("Check-in date cannot be equal to check-out date.")
                        .build();
            }

            List<Room> roomList = roomRepository.findAvailableRooms(checkInDate, checkOutDate, roomType);
            if (roomList.isEmpty()) {
                return ResponseDTO.builder()
                        .status(404)
                        .message("No available rooms found for the selected dates and room type.")
                        .build();
            }
            List<RoomDTO> roomDTOs = roomList.stream()
                    .map(room -> modelMapper.map(room, RoomDTO.class))
                    .toList();

            return ResponseDTO.builder()
                    .status(200)
                    .message("Available rooms retrieved successfully.")
                    .rooms(roomDTOs)
                    .build();

        } catch (Exception e) {
            return ResponseDTO.builder()
                    .status(500)
                    .message("An error occurred while retrieving available rooms: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
        return Arrays.asList(RoomType.values());
//        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public ResponseDTO searchRoom(String input) {
        try {
            List<Room> roomList = roomRepository.searchRooms(input);
            if (roomList.isEmpty()) {
                return ResponseDTO.builder()
                        .status(404)
                        .message("No rooms found matching the search criteria.")
                        .build();
            }
            List<RoomDTO> roomDTOs = roomList.stream()
                    .map(room -> modelMapper.map(room, RoomDTO.class))
                    .toList();
            return ResponseDTO.builder()
                    .status(200)
                    .message("Rooms found matching the search criteria.")
                    .rooms(roomDTOs)
                    .build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .status(500)
                    .message("An error occurred while searching for rooms: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseDTO deleteRoom(Long roomId) {
        try {
            if (!roomRepository.existsById(roomId)) {
                return ResponseDTO.builder()
                        .status(404)
                        .message("Room not found with id: " + roomId)
                        .build();
            }
            roomRepository.deleteById(roomId);
            return ResponseDTO.builder()
                    .status(200)
                    .message("Room deleted successfully. (id: " + roomId + ")")
                    .build();
        } catch (Exception e) {
            return ResponseDTO.builder()
                    .status(500)
                    .message("An error occurred while deleting the room: " + e.getMessage())
                    .build();
        }
    }

    private String saveImage(MultipartFile imageFile) {
        if (!imageFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }
        File directory = new File(IMAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        String imagePath = IMAGE_DIRECTORY + fileName;
        try {
            File destiationFile = new File(imagePath);
            imageFile.transferTo(destiationFile);
        } catch (Exception e) {
            log.error("Error saving image: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to save image", e);
        }
        return imagePath;
    }
}