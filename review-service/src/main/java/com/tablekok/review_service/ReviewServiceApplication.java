package com.tablekok.review_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class ReviewServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewServiceApplication.class, args);
	}

}
