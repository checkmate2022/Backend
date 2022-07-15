package com.checkmate.backend.entity.chat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private MessageType type; // 메시지 타입
	private String sender; // 메시지 보낸사람
	private String message; // 메시지
	private String roomId;

	public static ChatMessage createChatMessage(String roomId, String sender, String message, MessageType type) {
		ChatMessage chatMessage = ChatMessage.builder()
			.roomId(roomId)
			.sender(sender)
			.message(message)
			.type(type)
			.build();
		return chatMessage;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	// 메시지 타입 : 입장, 퇴장, 채팅
	public enum MessageType {
		ENTER, QUIT, TALK
	}

}