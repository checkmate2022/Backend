package com.checkmate.backend.controller;

import java.io.IOException;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.backend.entity.chat.ChatMessage;
import com.checkmate.backend.service.ChatService;
import com.checkmate.backend.service.FCMService;
import com.checkmate.backend.service.pubsub.RedisPublisher;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@CrossOrigin
@Transactional
public class ChattingController {
	private final RedisPublisher redisPublisher;
	private final ChatService chatService;
	private final FCMService fcmService;

	/**
	 * websocket "/chat/pub/message"로 들어오는 메시징을 처리한다.
	 */
	@Operation(summary = "채팅방 메시지", description = "메시지")
	@MessageMapping("/message")
	public void message(ChatMessage message) throws IOException {
		// 채팅방 입장시에는 대화명과 메시지를 자동으로 세팅한다.
		log.info("채팅 메시지");
		if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
			chatService.enterChatRoom(message.getRoomId());
			message.setSender("[알림] " + message.getSender());
			message.setMessage(message.getSender() + "님이 입장하셨습니다.");
		} else if (ChatMessage.MessageType.QUIT.equals(message.getType())) {
			message.setSender("[알림] " + message.getSender());
			message.setMessage(message.getSender() + "님이 퇴장하셨습니다.");
			chatService.deleteById(message.getRoomId());
		}
		chatService.save(message);
		// Websocket에 발행된 메시지를 redis로 발행(publish)
		ChannelTopic topic = chatService.getTopic(message.getRoomId());
		redisPublisher.publish(topic, message);
		fcmService.sendMessageTo(
			message.getReceiver(),
			message.getSender() + " 답장이 왔습니다.",
			message.getMessage());
	}

}