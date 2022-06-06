package com.checkmate.backend.config.oauth;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @package : com.checkmate.backend.oauth.config
 * @name: AppProperties.java
 * @date : 2022/05/23 1:47 오전
 * @author : jifrozen
 * @version : 1.0.0
 * @description : 정의한 토큰 정보를 자바 코드에서 가져다 쓰기 위함
 * @modified :
 **/
@Getter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

	private final Auth auth = new Auth();
	private final OAuth2 oauth2 = new OAuth2();

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Auth {
		private String tokenSecret;
		private long tokenExpiry;
		private long refreshTokenExpiry;
	}

	public static final class OAuth2 {
		private List<String> authorizedRedirectUris = new ArrayList<>();

		public List<String> getAuthorizedRedirectUris() {
			return authorizedRedirectUris;
		}

		public OAuth2 authorizedRedirectUris(List<String> authorizedRedirectUris) {
			this.authorizedRedirectUris = authorizedRedirectUris;
			return this;
		}
	}
}
