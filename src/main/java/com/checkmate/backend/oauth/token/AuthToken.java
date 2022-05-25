package com.checkmate.backend.oauth.token;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @package : com.checkmate.backend.oauth.token
 * @name: AuthToken.java
 * @date : 2022/05/22 5:13 오후
 * @author : jifrozen
 * @version : 1.0.0
 * @description : Jwt 패키지를 생성, 토큰 생성과 토큰의 유효성 검증 담당
 * @modified :
 **/
@Slf4j
@RequiredArgsConstructor
public class AuthToken {

	private static final String AUTHORITIES_KEY = "role";
	@Getter
	private final String token;
	private final Key key;

	AuthToken(String id, Date expiry, Key key) {
		this.key = key;
		this.token = createAuthToken(id, expiry);
	}

	AuthToken(String id, String role, Date expiry, Key key) {
		this.key = key;
		this.token = createAuthToken(id, role, expiry);
	}

	//토큰 생성
	private String createAuthToken(String id, Date expiry) {
		return Jwts.builder()
			.setSubject(id)
			.signWith(key, SignatureAlgorithm.HS256)
			.setExpiration(expiry)
			.compact();
	}

	private String createAuthToken(String id, String role, Date expiry) {
		return Jwts.builder()
			.setSubject(id)
			.claim(AUTHORITIES_KEY, role)
			.signWith(key, SignatureAlgorithm.HS256)
			.setExpiration(expiry)
			.compact();
	}

	public boolean validate() {
		return this.getTokenClaims() != null;
	}

	public Claims getTokenClaims() {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (SecurityException e) {
			log.info("Invalid JWT signature.");
		} catch (MalformedJwtException e) {
			log.info("Invalid JWT token.");
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token.");
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT token.");
		} catch (IllegalArgumentException e) {
			log.info("JWT token compact of handler are invalid.");
		}
		return null;
	}

	public Claims getExpiredTokenClaims() {
		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			log.info("expired JWT token.");
			return e.getClaims();
		}
		return null;
	}
}
