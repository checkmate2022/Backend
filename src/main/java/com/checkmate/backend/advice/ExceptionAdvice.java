package com.checkmate.backend.advice;

import java.io.FileNotFoundException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.checkmate.backend.advice.exception.DuplicateException;
import com.checkmate.backend.advice.exception.LoginFailedException;
import com.checkmate.backend.advice.exception.MaximumException;
import com.checkmate.backend.advice.exception.OAuthProviderMissMatchException;
import com.checkmate.backend.advice.exception.ResourceNotExistException;
import com.checkmate.backend.advice.exception.TokenValidFailedException;
import com.checkmate.backend.advice.exception.UserNotFoundException;
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
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected CommonResult loginFailedException(HttpServletRequest request, LoginFailedException e) {
		return responseService.getFailResult(HttpStatus.BAD_REQUEST.value(), "로그인 실패했습니다.");
	}

	@ExceptionHandler(TokenValidFailedException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	protected CommonResult tokenValidFailedException(HttpServletRequest request, TokenValidFailedException e) {
		return responseService.getFailResult(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
	}

	@ExceptionHandler(OAuthProviderMissMatchException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected CommonResult oAuthProviderMissMatchException(HttpServletRequest request,
		OAuthProviderMissMatchException e) {
		return responseService.getFailResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), "");
	}

	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected CommonResult userNotFoundException(HttpServletRequest request, UserNotFoundException e) {
		return responseService.getFailResult(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}

	@ExceptionHandler(ResourceNotExistException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected CommonResult resourceNotExistException(HttpServletRequest request, ResourceNotExistException e) {
		return responseService.getFailResult(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}

	@ExceptionHandler(DuplicateException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected CommonResult duplicateException(HttpServletRequest request, DuplicateException e) {
		return responseService.getFailResult(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}

	@ExceptionHandler(MaximumException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected CommonResult maximumException(HttpServletRequest request, MaximumException e) {
		return responseService.getFailResult(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}

	@ExceptionHandler(FileNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected CommonResult fileNotFoundException(HttpServletRequest request, FileNotFoundException e) {
		return responseService.getFailResult(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}

}
