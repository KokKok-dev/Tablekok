package com.tablekok.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableFeignClients
@EnableJpaAuditing
@SpringBootApplication(
	scanBasePackages = {
		"com.tablekok.user_service",     // 현재 서비스 패키지 스캔
		"com.tablekok"                   // Common 모듈
	}
)
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}