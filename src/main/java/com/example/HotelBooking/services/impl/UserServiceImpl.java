package com.example.HotelBooking.services.impl;

import com.example.HotelBooking.dtos.*;
import com.example.HotelBooking.entities.Booking;
import com.example.HotelBooking.entities.User;
import com.example.HotelBooking.entities.UserRole;
import com.example.HotelBooking.exceptions.InValidCredentialException;
import com.example.HotelBooking.exceptions.NotFoundException;
import com.example.HotelBooking.repositories.BookingRepository;
import com.example.HotelBooking.repositories.UserRepository;
import com.example.HotelBooking.security.JwtUtils;
import com.example.HotelBooking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;
    @Override
    public ResponseDTO registerUser(RegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new InValidCredentialException("Email already exists");
        }
        UserRole role = registrationRequest.getUserRole() != null ? registrationRequest.getUserRole() : UserRole.CUSTOMER;
        User user = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .phoneNumber(registrationRequest.getPhoneNumber())
                .userRole(role)
                .build();
        log.info("Registering user: {}", user);
        userRepository.save(user);
        return ResponseDTO.builder()
                .message("User registered successfully")
                .status(200)
                .build();
    }

    @Override
    public ResponseDTO loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + loginRequest.getEmail()));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InValidCredentialException("Invalid password");
        }
        String token = jwtUtils.generateToken(user.getEmail());
        return ResponseDTO.builder()
                .message("Welcome back " + user.getFirstName()+ " " + user.getLastName())
                .userRole(user.getUserRole())
                .token(token)
                .isActive(user.isActive())
                .expirationTime("6 months")
                .status(200)
                .build();
    }

    @Override
    public ResponseDTO getAllUsers() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<UserDTO> userDTOs = users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();

        return ResponseDTO.builder()
                .status(200)
                .message("success")
                .users(userDTOs)
                .build();
    }

    @Override
    public ResponseDTO getOwnAccountDetails() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        if(userDTO == null) {
            throw new NotFoundException("User not found with email: " + email);
        }
        return ResponseDTO.builder()
                .message("successfully retrieved own account details")
                .status(200)
                .user(userDTO)
                .build();
    }

    @Override
    public User getCurrentLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        return user;
    }

    @Override
    public ResponseDTO updateOwnAccount(UserDTO userDTO) {
        User user = getCurrentLoggedInUser();
        log.info("Updating user: {}", user);
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        userRepository.save(user);
        return ResponseDTO.builder()
                .message("User updated successfully")
                .status(200)
                .build();
    }

    @Override
    public ResponseDTO deleteOwnAccount() {
        User user = getCurrentLoggedInUser();
        log.info("Deleting user: {}", user);
        userRepository.delete(user);
        return ResponseDTO.builder()
                .message("Successfully delted" + user.getFirstName() + " " + user.getLastName())
                .status(200)
                .build();
    }

    @Override
    public ResponseDTO getMyBookingHistory() {
    User user = getCurrentLoggedInUser();
    if (user != null) {
        List<Booking> bookings = bookingRepository.findByUserId(user.getId());
        List<BookingDTO> bookingDTOs = modelMapper.map(bookings, new TypeToken<List<BookingDTO>>(){}.getType());
        if(bookingDTOs.isEmpty()) {
            return ResponseDTO.builder()
                    .message("No bookings found for the user")
                    .status(404)
                    .build();
        }
        return ResponseDTO.builder()
                .message("Booking history retrieved successfully")
                .status(200)
                .bookings(bookingDTOs)
                .build();
    }
    return ResponseDTO.builder()
                .message("No bookings found for the user")
                .status(404)
                .build();
    }
}
