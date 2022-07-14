package com.checkmate.backend.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.checkmate.backend.entity.chat.ChatRoom;
import com.checkmate.backend.entity.user.User;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,String> {

	@Query("select c from ChatRoom c where c.id=:id")
	Optional<ChatRoom> findById(@Param("id") String id);
	@Query("select c from ChatRoom c where c.receiver=:user or c.sender=:user")
	List<ChatRoom> findChatRoomsByUser(@Param("user") User user);

}