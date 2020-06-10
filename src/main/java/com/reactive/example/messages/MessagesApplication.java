package com.reactive.example.messages;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MessagesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessagesApplication.class, args);
	}

}
