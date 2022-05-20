package com.checkmate.backend.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * @package : com.checkmate.backend.config
 * @name: Swagger2Config.java
 * @date : 2022/05/19 6:49 오후
 * @author : jifrozen
 * @version : 1.0.0
 * @description : Swagger 문서 config 파일
 * @modified :
 **/
@Configuration
public class Swagger2Config {

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
			.group("v1")
			.pathsToMatch("/api/**")
			.build();
	}

	@Bean
	public OpenAPI springShopOpenAPI() {
		return new OpenAPI()
			.info(new Info().title("CheckMate API")
				.description("CheckMate API 명세서입니다.")
				.version("v0.0.1"));
	}
}
