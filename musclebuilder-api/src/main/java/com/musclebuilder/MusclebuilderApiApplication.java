package com.musclebuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class MusclebuilderApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusclebuilderApiApplication.class, args);
	}

}
