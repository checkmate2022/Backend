package com.checkmate.backend.advice.exception;

public class DuplicateException extends RuntimeException {
	public DuplicateException() {
		super();
	}

	public DuplicateException(String message) {
		super(message);
	}

	public DuplicateException(String message, Throwable cause) {
		super(message, cause);
	}
}
