package com.example.find_it;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FindItApplication {

	public static void main(String[] args) {
		SpringApplication.run(FindItApplication.class, args);
	}

}
