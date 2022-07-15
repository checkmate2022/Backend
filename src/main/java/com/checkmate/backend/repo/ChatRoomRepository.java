package com.checkmate.backend.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.checkmate.backend.entity.chat.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

	@Query("select c from ChatRoom c where c.id=:id")
	Optional<ChatRoom> findById(@Param("id") String id);

	@Query("select c from ChatRoom c where c.username1=:user or c.username2=:user")
	List<ChatRoom> findChatRoomsByUser(@Param("user") String user);

}