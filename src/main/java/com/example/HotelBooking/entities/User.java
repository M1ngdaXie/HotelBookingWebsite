package com.example.HotelBooking.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "email is required")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "password is required")
    private String password;
    private String firstName;
    private String lastName;

    @NotBlank(message = "phoneNumber is required")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private boolean isActive;
    private final LocalDateTime createdAt = LocalDateTime.now();

}
