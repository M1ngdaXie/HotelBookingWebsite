package com.example.HotelBooking;

import com.example.HotelBooking.dtos.NotificationDTO;
import com.example.HotelBooking.entities.NotificationType;
import com.example.HotelBooking.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableAsync

public class HotelBookingApplication  {

	public static void main(String[] args) {
		SpringApplication.run(HotelBookingApplication.class, args);
	}



}
