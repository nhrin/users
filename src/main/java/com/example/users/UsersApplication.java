package com.example.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class UsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersApplication.class, args);
	}

}
