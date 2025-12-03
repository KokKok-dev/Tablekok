package com.tablekok.review_service.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "reservation-service")
public interface ReservationFeignClient {
}
