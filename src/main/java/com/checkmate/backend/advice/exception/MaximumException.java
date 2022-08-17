package com.checkmate.backend.advice.exception;

public class MaximumException extends RuntimeException {
	public MaximumException() {
		super();
	}

	public MaximumException(String message) {
		super(message);
	}

	public MaximumException(String message, Throwable cause) {
		super(message, cause);
	}
}
