package com.tablekok.waiting_server.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tablekok.waiting_server.infrastructure.client.feign.FeignErrorDecoder;

import feign.codec.ErrorDecoder;

@Configuration
public class FeignConfig {
	@Bean
	public ErrorDecoder errorDecoder() {
		return new FeignErrorDecoder();
	}
}
