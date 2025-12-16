package com.tablekok.gateway_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway.uris")
public record GatewayUriProperties(
	String review,
	String user,
	String store,
	String reservation,
	String search,
	String hotReservation
) {
}
