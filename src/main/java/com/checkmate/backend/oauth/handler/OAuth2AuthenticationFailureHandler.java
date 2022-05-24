package com.checkmate.backend.oauth.handler;

import static com.checkmate.backend.oauth.api.repo.OAuth2AuthorizationRequestBasedOnCookieRepository.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.checkmate.backend.oauth.api.repo.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.checkmate.backend.oauth.util.CookieUtil;

import lombok.RequiredArgsConstructor;

/**
 * @package : com.checkmate.backend.oauth.handler
 * @name: OAuth2AuthenticationFailureHandler.java
 * @date : 2022/05/23 1:26 오전
 * @author : jifrozen
 * @version : 1.0.0
 * @description : OAuth2 authentication 수행중에 어떠한 error라도 발생하게되면, OAuth2AuthenticationFailureHandler가 invoke
 * @modified :
 **/
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws
		IOException, ServletException {
		String targetUrl = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
			.map(Cookie::getValue)
			.orElse(("/"));

		exception.printStackTrace();

		targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
			.queryParam("error", exception.getLocalizedMessage())
			.build().toUriString();

		authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}