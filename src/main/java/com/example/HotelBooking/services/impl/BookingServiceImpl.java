package com.example.HotelBooking.services.impl;

import com.example.HotelBooking.dtos.BookingDTO;
import com.example.HotelBooking.dtos.NotificationDTO;
import com.example.HotelBooking.dtos.ResponseDTO;
import com.example.HotelBooking.entities.*;
import com.example.HotelBooking.exceptions.InValidBookingAndDateException;
import com.example.HotelBooking.exceptions.NotFoundException;
import com.example.HotelBooking.repositories.BookingRepository;
import com.example.HotelBooking.repositories.RoomRepository;
import com.example.HotelBooking.services.BookingCodeGenerator;
import com.example.HotelBooking.services.BookingService;
import com.example.HotelBooking.services.NotificationService;
import com.example.HotelBooking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final BookingCodeGenerator bookingCodeGenerator;

    @Override
    public ResponseDTO getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<BookingDTO> bookingDTOs = bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
                .toList();
        if (bookingDTOs.isEmpty()) {
            return ResponseDTO.builder()
                    .status(404)
                    .message("No bookings found.")
                    .build();
        }
        for( BookingDTO bookingDTO : bookingDTOs) {
            bookingDTO.setUser(null);
            bookingDTO.setRoom(null);
        }

        return ResponseDTO.builder()
                .status(200)
                .message("Bookings retrieved successfully.")
                .bookings(bookingDTOs)
                .build();
    }

    @Override
    public ResponseDTO createBooking(BookingDTO bookingDTO) {
        User currentUser = userService.getCurrentLoggedInUser();
        Room room = roomRepository.findById(bookingDTO.getRoomId())
                .orElseThrow(() -> new NotFoundException("Room not found with ID: " + bookingDTO.getRoomId()));

        if (bookingDTO.getCheckInDate().isBefore(LocalDate.now())) {
            return ResponseDTO.builder()
                    .status(400)
                    .message("Check-in date cannot be in the past.")
                    .build();
        }
        if (bookingDTO.getCheckOutDate().isBefore(bookingDTO.getCheckInDate())) {
            return ResponseDTO.builder()
                    .status(400)
                    .message("Check-out date cannot be before check-in date.")
                    .build();
        }
        if (bookingDTO.getCheckInDate().isEqual(bookingDTO.getCheckOutDate())) {
            return ResponseDTO.builder()
                    .status(400)
                    .message("Check-in date cannot be equal to check-out date.")
                    .build();
        }
        boolean isRoomAvailable = bookingRepository.isRoomAvailable(
                bookingDTO.getRoomId(),
                bookingDTO.getCheckInDate(),
                bookingDTO.getCheckOutDate()
        );
        if (!isRoomAvailable) {
            throw new InValidBookingAndDateException("Room is not available for the selected dates.");
        }
        BigDecimal totalPrice = caculateTotalPrice(room, bookingDTO);
        String bookingReference = bookingCodeGenerator.generateBookingReferenceCode();
        Booking booking = new Booking();
        booking.setUser(currentUser);
        booking.setRoom(room);
        booking.setPaymentStatus(bookingDTO.getPaymentStatus());
        booking.setCheckInDate(bookingDTO.getCheckInDate());
        booking.setCheckOutDate(bookingDTO.getCheckOutDate());
        booking.setTotalPrice(totalPrice);
        booking.setBookingReference(bookingReference);
        booking.setCreatedAt(LocalDate.now());
        booking.setBookingStatus(BookingStatus.BOOKED);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking = bookingRepository.save(booking);

        log.info("Booking created successfully with ID: {}", booking.getId());

        // Use the new email notification method
        sendBookingNotificationEmail(
                currentUser,
                bookingReference,
                totalPrice,
                "Booking created successfully. Please complete your payment.",
                String.format("Dear %s, your booking has been created successfully.", currentUser.getFirstName())
        );

        return ResponseDTO.builder()
                .status(200)
                .message("Booking created successfully with ID: " + booking.getId())
                .booking(bookingDTO)
                .build();
    }

    private BigDecimal caculateTotalPrice(Room room, BookingDTO bookingDTO) {
        BigDecimal pricePerNight = room.getPricePerNight();
        long numberOfNights = ChronoUnit.DAYS.between(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        return pricePerNight.multiply(BigDecimal.valueOf(numberOfNights));
    }

    @Override
    public ResponseDTO findBookingByReferenceNo(String bookingReference) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking not found with reference: " + bookingReference));

        BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
        return ResponseDTO.builder()
                .status(200)
                .message("Booking found successfully.")
                .booking(bookingDTO)
                .build();
    }

    @Override
    public ResponseDTO updateBooking(BookingDTO bookingDTO) {
        if(bookingDTO.getId() == null){
            return ResponseDTO.builder()
                    .status(400)
                    .message("Booking ID is required for update.")
                    .build();
        }
        Room room = roomRepository.findById(bookingDTO.getId())
                .orElseThrow(() -> new NotFoundException("Room not found with ID: " + bookingDTO.getRoomId()));
        Booking existingBooking = bookingRepository.findById(bookingDTO.getId())
                .orElseThrow(() -> new NotFoundException("Booking not found with ID: " + bookingDTO.getId()));

        if(bookingDTO.getBookingStatus() != null) {
            existingBooking.setBookingStatus(bookingDTO.getBookingStatus());
        }
        if(bookingDTO.getPaymentStatus() != null) {
            existingBooking.setPaymentStatus(bookingDTO.getPaymentStatus());
        }
        if(bookingDTO.getCheckInDate() != null) {
            existingBooking.setCheckInDate(bookingDTO.getCheckInDate());
        }
        if(bookingDTO.getCheckOutDate() != null) {
            existingBooking.setCheckOutDate(bookingDTO.getCheckOutDate());
        }
        BookingDTO bookingDTOPrice = modelMapper.map(existingBooking, BookingDTO.class);

            System.out.println("Calculating total price: from "+ bookingDTOPrice.getCheckInDate() + " to " + bookingDTOPrice.getCheckOutDate());
            existingBooking.setTotalPrice(caculateTotalPrice(room, bookingDTOPrice));

        bookingRepository.save(existingBooking);

        // Send notification email
        User user = existingBooking.getUser();
        String bookingReference = existingBooking.getBookingReference();
        BigDecimal totalPrice = existingBooking.getTotalPrice();
        String subject = "Booking updated successfully";
        String bodyPrefix = String.format("Dear %s, your booking has been updated successfully.", user.getFirstName());
        sendBookingNotificationEmail(user, bookingReference, totalPrice, subject, bodyPrefix);

        return ResponseDTO.builder()
                .status(200)
                .message("Booking updated successfully. Please recheck your email for details.")
                .build();
    }

    @Override
    public ResponseDTO deleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with ID: " + bookingId));
        User currentUser = userService.getCurrentLoggedInUser();
        if (!currentUser.getId().equals(booking.getUser().getId())) {
            return ResponseDTO.builder()
                    .status(403)
                    .message("You are not authorized to delete this booking.")
                    .build();
        }
        bookingRepository.deleteById(bookingId);
        return ResponseDTO.builder()
                .status(200)
                .message("Booking deleted successfully with ID: " + bookingId)
                .build();
    }
    private void sendBookingNotificationEmail(User user, String bookingReference, BigDecimal totalPrice, String subject, String bodyPrefix) {
        String paymentUrl = "http://localhost:3000/payment/" + bookingReference + "/" + totalPrice;
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject(subject)
                .bookingReference(bookingReference)
                .body(String.format("%s\n\nPlease complete your payment using the following link: %s", bodyPrefix, paymentUrl))
                .build();
        notificationService.sendEmail(notificationDTO);
    }
    @Override
    public ResponseDTO findBookingByCurrentUser() {
        User currentUser = userService.getCurrentLoggedInUser();
        List<Booking> bookings = bookingRepository.findByUserId(currentUser.getId());
        List<BookingDTO> bookingDTOs = bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
                .toList();
        if (bookingDTOs.isEmpty()) {
            return ResponseDTO.builder()
                    .status(404)
                    .message("No bookings found for current user.")
                    .build();
        }
        return ResponseDTO.builder()
                .status(200)
                .message("Bookings retrieved successfully.")
                .bookings(bookingDTOs)
                .build();
    }
}
