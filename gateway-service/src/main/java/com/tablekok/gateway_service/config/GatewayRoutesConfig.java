package com.tablekok.gateway_service.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tablekok.gateway_service.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(GatewayUriProperties.class)
public class GatewayRoutesConfig {

	private final GatewayUriProperties uris;

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtAuthenticationFilter jwtFilter) {
		return builder.routes()
			// Review Service
			.route("review-service-store-nested", r -> r
				.path("/v1/stores/*/reviews/**") // 가게 리뷰 (와일드카드 사용)
				.filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
				.uri(uris.review())
			)
			.route("review-service-user-nested", r -> r
				.path("/v1/users/me/reviews/**") // 내 리뷰
				.filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
				.uri(uris.review())
			)
			.route("review-service-base", r -> r
				.path("/v1/reviews/**") // 일반 리뷰
				.filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
				.uri(uris.review())
			)

			// User Service - 인증 관련 (JWT 필터 없음)
			.route("user-service-auth", r -> r
				.path("/v1/auth/**")
				.uri(uris.user())
			)

			// User Service - 사용자 관리 (JWT 필터 필요)
			.route("user-service", r -> r
				.path("/v1/users/**", "/internal/v1/users/**")
				.filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
				.uri(uris.user())
			)

			// Store Service (리뷰 관련 경로가 아닌 나머지)
			.route("store-service", r -> r
				.path("/v1/stores/**", "/v1/categories/**")
				.filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
				.uri(uris.store())
			)

			// Reservation Service
			.route("reservation-service", r -> r
				.path("/v1/reservations/**")
				.filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
				.uri(uris.reservation())
			)

			// Search Service
			.route("search-service", r -> r
				.path("/v1/search/**")
				.filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
				.uri(uris.search())
			)

			// Hot Reservation Service
			.route("hot-reservation-service", r -> r
				.path("/v1/hot-reservations/**")
				.filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
				.uri(uris.hotReservation())
			)

			.build();
	}
}
