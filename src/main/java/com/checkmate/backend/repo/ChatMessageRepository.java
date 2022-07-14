package com.checkmate.backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.chat.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

	List<ChatMessage> getChatMessagesByRoomId(String roomId);

}
