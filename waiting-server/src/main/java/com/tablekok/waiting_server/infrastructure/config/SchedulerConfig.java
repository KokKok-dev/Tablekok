package com.tablekok.waiting_server.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {
	@Bean
	public TaskScheduler taskScheduler() {
		// 작업을 처리할 스레드 풀 크기 설정 (10개)
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(10);
		scheduler.setThreadNamePrefix("NoShow-Scheduler-");
		scheduler.initialize();
		return scheduler;
	}
}
