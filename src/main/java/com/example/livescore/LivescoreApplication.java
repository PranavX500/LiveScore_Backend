package com.example.livescore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin

public class LivescoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivescoreApplication.class, args);
	}

}
