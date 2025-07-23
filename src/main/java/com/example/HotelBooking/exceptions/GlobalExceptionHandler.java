package com.example.HotelBooking.exceptions;

import com.example.HotelBooking.dtos.ResponseDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.eclipse.angus.mail.iap.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@NoArgsConstructor
public class GlobalExceptionHandler   {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleAllUnknownExceptions (Exception ex) {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseDTO> handleNotFoundExceptions (NotFoundException ex) {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(NameValueRequiredException.class)
    public ResponseEntity<ResponseDTO>  handleNameValueRequiredException(NameValueRequiredException ex) {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InValidCredentialException.class)
    public ResponseEntity<ResponseDTO> handleInValidCredentialException(InValidCredentialException ex) {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .message(ex.getMessage())
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
    }
}
