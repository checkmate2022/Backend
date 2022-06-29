package com.checkmate.backend.oauth.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.checkmate.backend.advice.RestAuthenticationEntryPoint;
import com.checkmate.backend.oauth.api.repo.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.checkmate.backend.oauth.api.repo.UserRefreshTokenRepository;
import com.checkmate.backend.oauth.filter.TokenAuthenticationFilter;
import com.checkmate.backend.oauth.handler.OAuth2AuthenticationFailureHandler;
import com.checkmate.backend.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.checkmate.backend.oauth.handler.TokenAccessDeniedHandler;
import com.checkmate.backend.oauth.service.CustomOAuth2UserService;
import com.checkmate.backend.oauth.service.CustomUserDetailService;
import com.checkmate.backend.oauth.token.AuthTokenProvider;

import lombok.RequiredArgsConstructor;

/**
 * @package : com.checkmate.backend.oauth.config
 * @name: SecurityConfig.java
 * @date : 2022/05/23 1:51 오전
 * @author : jifrozen
 * @version : 1.0.0
 * @description : Spring security를 위한 설정 파일
 * @modified :
 **/
@Component
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final CorsProperties corsProperties;
	private final AppProperties appProperties;
	private final AuthTokenProvider tokenProvider;
	private final CustomUserDetailService userDetailsService;
	private final CustomOAuth2UserService oAuth2UserService;
	private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
	private final UserRefreshTokenRepository userRefreshTokenRepository;

	/*
	 * UserDetailsService 설정
	 * */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.
			headers().frameOptions().sameOrigin()
			// .and()
			// .headers().httpStrictTransportSecurity().disable()
			.and()
			.cors()//cors 허용
			.and()
			.sessionManagement() // session Creation Policy를 STATELESS로 정의해 session을 사용하지 않겠다 선언
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			//REST API 이기 떄문에 사용 x
			.csrf().disable()
			.formLogin().disable()
			.httpBasic().disable()
			.exceptionHandling()
			// 사용자가 authentication 없이 protected resource에 접근하는 경우에 invoked 되는 entry point
			.authenticationEntryPoint(new RestAuthenticationEntryPoint())
			//인가되지않은 사용자 Handler
			.accessDeniedHandler(tokenAccessDeniedHandler)
			.and()
			// 경로 허용 경우 설정
			.authorizeRequests()
			// .requestMatchers().permitAll()
			// .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
			.antMatchers("/**", "/h2-console/**", "/api/**").permitAll()
			// .antMatchers("/api/**","**/oauth/**").permitAll()
			// .antMatchers("/api/**").hasAnyAuthority(RoleType.USER.getCode())
			// .antMatchers("/api/**/admin/**").hasAnyAuthority(RoleType.ADMIN.getCode())
			// .anyRequest().authenticated()
			.and()
			//OAuth2 로그인 설정 부분
			.oauth2Login()
			// oauth 로그인시 접근할 end point를 정의합니다
			.authorizationEndpoint()
			.baseUri("/oauth2/authorization")
			.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
			.and()
			.redirectionEndpoint()
			.baseUri("/*/oauth2/code/*")
			.and()
			// 로그인시 사용할 User Service를 정의
			.userInfoEndpoint()
			.userService(oAuth2UserService)
			.and()
			//성공 실패 Handler
			.successHandler(oAuth2AuthenticationSuccessHandler())
			.failureHandler(oAuth2AuthenticationFailureHandler());

		//reqeust 요청이 올때마다 UsernamePasswordAuthenticationFilter 이전에 tokenAuthenticaitonFilter를 수행하도록 정의
		http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	/*
	 * auth 매니저 설정
	 * */
	@Override
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	/*
	 * security 설정 시, 사용할 인코더 설정
	 * */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	 * 토큰 필터 설정
	 * */
	@Bean
	public TokenAuthenticationFilter tokenAuthenticationFilter() {
		return new TokenAuthenticationFilter(tokenProvider);
	}

	/*
	 * 쿠키 기반 인가 Repository
	 * 인가 응답을 연계 하고 검증할 때 사용.
	 * */
	@Bean
	public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
		return new OAuth2AuthorizationRequestBasedOnCookieRepository();
	}

	/*
	 * Oauth 인증 성공 핸들러
	 * */
	@Bean
	public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
		return new OAuth2AuthenticationSuccessHandler(
			tokenProvider,
			appProperties,
			userRefreshTokenRepository,
			oAuth2AuthorizationRequestBasedOnCookieRepository()
		);
	}

	/*
	 * Oauth 인증 실패 핸들러
	 * */
	@Bean
	public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
		return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestBasedOnCookieRepository());
	}

	/*
	 * Cors 설정
	 * */
	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();

		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
		corsConfig.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
		corsConfig.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
		corsConfig.addAllowedOrigin("http://localhost:8080/h2-console");
		corsConfig.addAllowedOrigin("http://localhost:8080/h2-console/**");
		corsConfig.addAllowedOrigin("http://localhost:3000");

		corsConfig.setAllowCredentials(true);
		corsConfig.setMaxAge(corsConfig.getMaxAge());

		corsConfigSource.registerCorsConfiguration("/**", corsConfig);
		return corsConfigSource;
	}
}