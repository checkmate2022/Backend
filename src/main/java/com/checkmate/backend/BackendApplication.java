package com.checkmate.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.checkmate.backend.config.oauth.AppProperties;
import com.checkmate.backend.config.oauth.CorsProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({
	CorsProperties.class,
	AppProperties.class
})
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
