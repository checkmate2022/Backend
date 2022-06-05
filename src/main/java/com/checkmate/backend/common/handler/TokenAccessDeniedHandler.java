package com.checkmate.backend.common.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import lombok.RequiredArgsConstructor;

/**
 * @package : com.checkmate.backend.oauth.handler
 * @name: TokenAccessDeniedHandler.java
 * @date : 2022/05/22 5:43 오후
 * @author : jifrozen
 * @version : 1.0.0
 * @description : 인가되지 않은 사용자 관한 처리
 * @modified :
 **/
@Component
@RequiredArgsConstructor
public class TokenAccessDeniedHandler implements AccessDeniedHandler {

	private final HandlerExceptionResolver handlerExceptionResolver;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws
		IOException {
		//response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
		handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
	}
}