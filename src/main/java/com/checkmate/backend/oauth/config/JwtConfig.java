package com.checkmate.backend.oauth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.checkmate.backend.oauth.token.AuthTokenProvider;

/**
 * @package : com.checkmate.backend.oauth.config
 * @name: JwtConfig.java
 * @date : 2022/05/22 5:38 오후
 * @author : jifrozen
 * @version : 1.0.0
 * @description : Jwt을 사용하기 위한 설정
 * @modified :
 **/
@Configuration
public class JwtConfig {
	@Value("${jwt.secret}")
	private String secret;

	@Bean
	public AuthTokenProvider jwtProvider() {
		return new AuthTokenProvider(secret);
	}
}