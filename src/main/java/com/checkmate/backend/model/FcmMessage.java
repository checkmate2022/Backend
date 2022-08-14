package com.checkmate.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@Data
public class FcmMessage {
	private boolean validateOnly;
	private Message message;

	@Builder
	@AllArgsConstructor
	@Getter
	@Data
	public static class Message {
		private Notification notification;
		private String token;
	}

	@Builder
	@AllArgsConstructor
	@Getter
	@Data
	public static class Notification {
		private String title;
		private String body;
		private String image;
	}
}