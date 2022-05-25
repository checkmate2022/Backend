package com.checkmate.backend.advice.exception;

public class OAuthProviderMissMatchException extends RuntimeException {
	public OAuthProviderMissMatchException() {
		super();
	}

	public OAuthProviderMissMatchException(String message) {
		super(message);
	}
}
