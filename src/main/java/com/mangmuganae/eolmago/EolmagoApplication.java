package com.mangmuganae.eolmago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class EolmagoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EolmagoApplication.class, args);
	}

}
