package com.checkmate.backend.advice.exception;

public class TokenValidFailedException extends RuntimeException {
	public TokenValidFailedException() {
	}

	public TokenValidFailedException(String message) {
		super(message);
	}
}
