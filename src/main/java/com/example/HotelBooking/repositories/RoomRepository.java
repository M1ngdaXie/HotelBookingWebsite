package com.example.HotelBooking.repositories;

import com.example.HotelBooking.entities.Room;
import com.example.HotelBooking.entities.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // Finds available rooms for the given period and (optionally) room type
    @Query("""
            SELECT r FROM Room r
            WHERE
                r.id NOT IN (
                    SELECT b.room.id
                    FROM Booking b
                    WHERE :checkInDate <= b.checkOutDate
                    AND :checkOutDate >= b.checkInDate
                    AND b.bookingStatus IN ('BOOKED', 'CHECKED_IN')
                )
                AND (:roomType IS NULL OR r.roomType = :roomType)
            """)
    List<Room> findAvailableRooms(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("roomType") RoomType roomType
    );

    // Searches rooms by number, type, price, capacity, or description (case-insensitive)
    @Query("""
            SELECT r FROM Room r
            WHERE CAST(r.roomNumber AS string) LIKE %:searchParam%
               OR LOWER(CAST(r.roomType AS string)) LIKE LOWER(:searchParam)
               OR CAST(r.pricePerNight AS string) LIKE %:searchParam%
               OR CAST(r.capacity AS string) LIKE %:searchParam%
               OR LOWER(r.description) LIKE LOWER(CONCAT('%', :searchParam, '%'))
            """)
    List<Room> searchRooms(@Param("searchParam") String searchParam);

    // Returns all distinct RoomTypes that actually exist in the Room table
//    @Query("SELECT DISTINCT r.roomType FROM Room r")
//    List<RoomType> findDistinctRoomTypes();

    // Checks if a room number already exists (for duplicate detection)
    boolean existsByRoomNumber(Integer roomNumber);
}