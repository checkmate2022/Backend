package com.checkmate.backend.controller;

import java.util.ArrayList;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.backend.common.ListResult;
import com.checkmate.backend.common.SingleResult;
import com.checkmate.backend.entity.chat.ChatMessage;
import com.checkmate.backend.entity.chat.ChatRoom;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.service.ChatService;
import com.checkmate.backend.service.ResponseService;
import com.checkmate.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Chat", description = "채팅 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat")
public class ChatRoomController {

	private final ChatService chatService;
	private final ResponseService responseService;
	private final UserService userService;

	@Operation(summary = "room 전체 조회", description = "채팅 룸 전체를 조회한다.", security = {
		@SecurityRequirement(name = "bearer-key")})
	@GetMapping("/rooms")
	public ListResult<ChatRoom> rooms() {
		return responseService.getListResult(chatService.findAllRoom());
	}

	@Operation(summary = "채팅방 개설", description = "채팅방을 개설한다.", security = {@SecurityRequirement(name = "bearer-key")})
	@PostMapping("/room")
	public SingleResult<ChatRoom> createRoom(String other) {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());
		return responseService.getSingleResult(chatService.createChatRoom(user, other));
	}

	@Operation(summary = "방 정보 보기", description = "방 정보")
	@GetMapping("/room/{roomId}/roomInfo")
	public SingleResult<ChatRoom> roomInfo(@PathVariable String roomId) {
		return responseService.getSingleResult(chatService.findRoomById(roomId));
	}

	@Operation(summary = "방별 메시지 보기", description = "방별 채팅 메시지 list")
	@GetMapping("/room/{roomId}")
	public ListResult<ChatMessage> roomChatMessage(@PathVariable String roomId) {
		return responseService.getListResult(chatService.chatMessageList(roomId));
	}

	@Operation(summary = "사용자 별 방 조회", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping("/user/room")
	public ListResult<ChatRoom> getRoomsByCustomer() {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());
		if (user == null) {
			return responseService.getListResult(new ArrayList<>());
		}
		return responseService.getListResult(chatService.getUserEnterRooms(user));
	}

}