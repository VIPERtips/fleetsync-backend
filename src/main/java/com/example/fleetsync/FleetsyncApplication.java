package com.example.fleetsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FleetsyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(FleetsyncApplication.class, args);
	}

}
