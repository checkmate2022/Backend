package com.checkmate.backend.advice;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.checkmate.backend.advice.exception.LoginFailedException;
import com.checkmate.backend.advice.exception.OAuthProviderMissMatchException;
import com.checkmate.backend.advice.exception.TokenValidFailedException;
import com.checkmate.backend.common.CommonResult;
import com.checkmate.backend.service.ResponseService;

import lombok.RequiredArgsConstructor;

/**
 * @package : com.checkmate.backend.advice
 * @name: ExceptionAdvice.java
 * @date : 2022/05/19 8:45 오후
 * @author : jifrozen
 * @version : 1.0.0
 * @description : Exception 처리
 * @modified :
 **/
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

	private final ResponseService responseService;

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected CommonResult defaultException(HttpServletRequest request, Exception e) {
		return responseService.getFailResult(500, "실패");
	}

	@ExceptionHandler(LoginFailedException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected CommonResult loginFailedException(HttpServletRequest request, LoginFailedException e) {
		return responseService.getFailResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), "로그인 실패했습니다.");
	}

	@ExceptionHandler(TokenValidFailedException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	protected CommonResult tokenValidFailedException(HttpServletRequest request, TokenValidFailedException e) {
		return responseService.getFailResult(HttpStatus.NOT_ACCEPTABLE.value(), "토큰 만들기 실패.");
	}

	@ExceptionHandler(OAuthProviderMissMatchException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected CommonResult oAuthProviderMissMatchException(HttpServletRequest request,
		OAuthProviderMissMatchException e) {
		return responseService.getFailResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), "");
	}
}
