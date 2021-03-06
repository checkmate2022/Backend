package com.checkmate.backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import com.checkmate.backend.advice.exception.ResourceNotExistException;
import com.checkmate.backend.entity.chat.ChatMessage;
import com.checkmate.backend.entity.chat.ChatRoom;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.repo.ChatMessageRepository;
import com.checkmate.backend.repo.ChatRoomRepository;
import com.checkmate.backend.repo.UserRepository;
import com.checkmate.backend.service.pubsub.RedisSubscriber;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ChatService {
	public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
	// Redis
	private static final String CHAT_ROOMS = "CHAT_ROOM";
	// 채팅방(topic)에 발행되는 메시지를 처리할 Listner
	private final RedisMessageListenerContainer redisMessageListener;
	// 구독 처리 서비스
	private final RedisSubscriber redisSubscriber;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ChatRoomRepository chatRoomRepository;
	private final ChatMessageRepository chatMessageRepository;
	private final UserRepository userRepository;
	private HashOperations<String, String, ChatRoom> opsHashChatRoom;
	// 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
	private Map<String, ChannelTopic> topics;
	private HashOperations<String, String, String> hashOpsEnterInfo;

	@PostConstruct
	private void init() {
		opsHashChatRoom = redisTemplate.opsForHash();
		hashOpsEnterInfo = redisTemplate.opsForHash();

		topics = new HashMap<>();
	}

	public List<ChatRoom> findAllRoom() {
		return chatRoomRepository.findAll();
	}

	public ChatRoom findRoomById(String id) {
		ChatRoom chatRoom = (ChatRoom)chatRoomRepository.findById(id).orElseThrow(ResourceNotExistException::new);
		return chatRoom;
	}

	/**
	 * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
	 */
	public ChatRoom createChatRoom(User sender, String other) {
		User o = userRepository.findByUsername(other);
		String name = sender.getUsername() + "와 " + o.getUsername();
		ChatRoom chatRoom = new ChatRoom(name, sender.getUsername(), sender.getUserImage(), o.getUsername(),
			o.getUserImage());
		opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getId(), chatRoom);
		chatRoomRepository.save(chatRoom);
		return chatRoom;
	}

	/**
	 * 채팅방 입장 : redis에 topic을 만들고 pub/sub 통신을 하기 위해 리스너를 설정한다.
	 */
	public void enterChatRoom(String roomId) {
		ChannelTopic topic = topics.get(roomId);
		if (topic == null)
			topic = new ChannelTopic(roomId);
		redisMessageListener.addMessageListener(redisSubscriber, topic);
		topics.put(roomId, topic);
	}

	public ChatMessage save(ChatMessage chatMessage) {
		return chatMessageRepository.save(chatMessage);
	}

	public ChannelTopic getTopic(String roomId) {
		return topics.get(roomId);
	}

	public List<ChatRoom> getUserEnterRooms(User me) {
		return chatRoomRepository.findChatRoomsByUser(me.getUsername());
	}

	public void deleteById(String roomId) {
		chatRoomRepository.deleteById(roomId);
	}

	public List<ChatMessage> chatMessageList(String roomId) {
		return chatMessageRepository.getChatMessagesByRoomId(roomId);
	}

	/**
	 * destination정보에서 roomId 추출
	 */
	public String getRoomId(String destination) {
		int lastIndex = destination.lastIndexOf('/');
		if (lastIndex != -1)
			return destination.substring(lastIndex + 1);
		else
			return "";
	}

	// 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
	public void setUserEnterInfo(String sessionId, String roomId) {
		hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
	}

	// 유저 세션으로 입장해 있는 채팅방 ID 조회
	public String getUserEnterRoomId(String sessionId) {
		return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
	}

	// 유저 세션정보와 맵핑된 채팅방ID 삭제
	public void removeUserEnterInfo(String sessionId) {
		hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
	}

}