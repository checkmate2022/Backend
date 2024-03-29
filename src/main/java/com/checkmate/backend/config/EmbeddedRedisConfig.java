package com.checkmate.backend.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import redis.embedded.RedisServer;

/**
 * 로컬 환경일경우 내장 레디스가 실행된다.
 */
@Profile("local")
@Configuration
public class EmbeddedRedisConfig {

	@Value("${spring.redis.port}")
	private int redisPort;

	private RedisServer redisServer;

	@PostConstruct
	public void redisServer() {
		redisServer = RedisServer.builder()
			.port(redisPort)
			.setting("maxmemory 128M")
			.build();
		try {
			redisServer.start();
		} catch (Exception e) {
		}
	}

	@PreDestroy
	public void stopRedis() {
		if (redisServer != null) {
			redisServer.stop();
		}
	}
}