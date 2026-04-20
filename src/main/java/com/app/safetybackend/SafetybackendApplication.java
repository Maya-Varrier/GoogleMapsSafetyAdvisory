package com.app.safetybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SafetybackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SafetybackendApplication.class, args);
	}

}
