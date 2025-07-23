package com.example.HotelBooking.services;

import com.example.HotelBooking.dtos.LoginRequest;
import com.example.HotelBooking.dtos.RegistrationRequest;
import com.example.HotelBooking.dtos.ResponseDTO;
import com.example.HotelBooking.dtos.UserDTO;
import com.example.HotelBooking.entities.User;

public interface UserService {
    ResponseDTO registerUser(RegistrationRequest registrationRequest);
    ResponseDTO loginUser(LoginRequest loginRequest);

    ResponseDTO getAllUsers();
    ResponseDTO getOwnAccountDetails();
    User getCurrentLoggedInUser();
    ResponseDTO updateOwnAccount(UserDTO userDTO);
    ResponseDTO deleteOwnAccount();

    ResponseDTO getMyBookingHistory();
}
