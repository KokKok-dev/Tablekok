package com.tablekok.hotreservationservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "search-service")
public interface SearchFeignClient {

}
