package com.tablekok.gateway_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@ConfigurationProperties(prefix = "gateway.uris")
@Getter
public class GatewayUriProperties {
	private String review;
	private String user;
	private String store;
	private String reservation;
	private String search;
}
