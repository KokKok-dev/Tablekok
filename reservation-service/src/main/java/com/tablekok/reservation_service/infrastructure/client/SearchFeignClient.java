package com.tablekok.reservation_service.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "search-service")
public interface SearchFeignClient {

}
