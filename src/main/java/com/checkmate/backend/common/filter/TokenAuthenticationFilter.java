package com.checkmate.backend.common.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.checkmate.backend.common.token.AuthToken;
import com.checkmate.backend.common.token.AuthTokenProvider;
import com.checkmate.backend.common.util.HeaderUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @package : com.checkmate.backend.oauth.filter
 * @name: TokenAuthenticationFilter.java
 * @date : 2022/05/22 5:38 오후
 * @author : jifrozen
 * @version : 1.0.0
 * @description : Generating Token / Validating JWT 과정에서 첫번째 필터 OncePerRequestFilter -> 토큰을 가지고 있는지 체크
 * @modified :
 **/
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

	private final AuthTokenProvider tokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String tokenStr = HeaderUtil.getAccessToken(request);
		AuthToken token = tokenProvider.convertAuthToken(tokenStr);

		if (token.validate()) {
			Authentication authentication = tokenProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);

	}
}
