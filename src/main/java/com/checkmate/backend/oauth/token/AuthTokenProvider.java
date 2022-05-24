package com.checkmate.backend.oauth.token;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.checkmate.backend.advice.exception.TokenValidFailedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthTokenProvider {

	private static final String AUTHORITIES_KEY = "role";
	private final Key key;

	public AuthTokenProvider(String secret) {
		// 스트링인 yml 파일의 secret키를 인코딩 된 byte 배열로 변환후 다시 SecretKey로 변환해 주는 hmacShaKeyFor
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public AuthToken createAuthToken(String id, Date expiry) {
		return new AuthToken(id, expiry, key);
	}

	public AuthToken createAuthToken(String id, String role, Date expiry) {
		return new AuthToken(id, role, expiry, key);
	}

	public AuthToken convertAuthToken(String token) {
		return new AuthToken(token, key);
	}

	// token 값에서 사용자의 정보를 꺼내는 함수
	public Authentication getAuthentication(AuthToken authToken) {

		if (authToken.validate()) {

			Claims claims = authToken.getTokenClaims();
			Collection<? extends GrantedAuthority> authorities =
				Arrays.stream(new String[] {claims.get(AUTHORITIES_KEY).toString()})
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());

			log.debug("claims subject := [{}]", claims.getSubject());
			User principal = new User(claims.getSubject(), "", authorities);

			return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
		} else {
			throw new TokenValidFailedException();
		}
	}

}
