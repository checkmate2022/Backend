package com.checkmate.backend.advice.exception;

public class MisMatchException extends RuntimeException {
	public MisMatchException(String msg, Throwable t) {
		super(msg, t);
	}

	public MisMatchException(String msg) {
		super(msg);
	}

	public MisMatchException() {
		super();
	}
}
