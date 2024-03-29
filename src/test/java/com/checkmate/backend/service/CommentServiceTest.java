package com.checkmate.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.checkmate.backend.advice.exception.ResourceNotExistException;
import com.checkmate.backend.advice.exception.UserNotFoundException;
import com.checkmate.backend.entity.board.Board;
import com.checkmate.backend.entity.channel.Channel;
import com.checkmate.backend.entity.comment.Comment;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.BoardDto;
import com.checkmate.backend.model.response.CommentResponse;
import com.checkmate.backend.repo.BoardRepository;
import com.checkmate.backend.repo.CommentRepository;
import com.checkmate.backend.repo.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentServiceTest 테스트")
class CommentServiceTest {
	CommentService commentService;
	@Mock
	CommentRepository commentRepository;
	@Mock
	FCMService fcmService;
	@Mock
	BoardRepository boardRepository;
	@Mock
	UserRepository userRepository;

	@BeforeEach
	void setUp() {
		this.commentService = new CommentService(this.commentRepository, this.fcmService, this.boardRepository,
			this.userRepository);
	}

	@Test
	void findAllByBoard() {
		User mockUser = User.builder().userSeq(1L).userId("repo1").build();
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("test")
			.teamDescription("test")
			.build();
		Channel mockChannel = new Channel("채널", mockTeam);
		Board board = new Board("제목", "내용", mockChannel, mockTeam.getTeamSeq(), mockUser.getUserSeq());

		List<Comment> commentList = new ArrayList<>();
		commentList.add(new Comment("내용", board, mockUser.getUserSeq(), "emoticon"));
		given(userRepository.findById(any())).willReturn(java.util.Optional.of(mockUser));
		given(boardRepository.findById(any())).willReturn(java.util.Optional.of(board));
		given(commentRepository.findAllByBoard(any())).willReturn(commentList);

		List<CommentResponse> commentResponses = commentService.findAllByBoard(1L);

		assertEquals(commentResponses.get(0).getContent(), "내용");
		assertEquals(commentResponses.get(0).getEmoticon(), "emoticon");
	}

	@Test
	void create() throws IOException {
		User mockUser = User.builder().userSeq(1L).userId("repo1").build();
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("test")
			.teamDescription("test")
			.build();
		Channel mockChannel = new Channel("채널", mockTeam);
		BoardDto boardDto = new BoardDto("제목", "내용");
		Board board = new Board("제목", "내용", mockChannel, mockTeam.getTeamSeq(), mockUser.getUserSeq());

		Comment comment = new Comment("내용", board, mockUser.getUserSeq(), "emoticon");
		given(userRepository.findById(any())).willReturn(java.util.Optional.of(mockUser));
		given(boardRepository.findById(any())).willReturn(java.util.Optional.of(board));
		given(commentRepository.save(any())).willReturn(comment);
		CommentResponse createComment = commentService.create("내용", 1L, "emoticon", mockUser);

		assertEquals(createComment.getContent(), comment.getContent());
	}

	@Test
	void update() {
		User mockUser = User.builder().userSeq(1L).userId("repo1").build();
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("test")
			.teamDescription("test")
			.build();
		Channel mockChannel = new Channel("채널", mockTeam);
		Board board = new Board("제목", "내용", mockChannel, mockTeam.getTeamSeq(), mockUser.getUserSeq());

		Comment comment = new Comment("내용", board, mockUser.getUserSeq(), "emoticon");
		given(commentRepository.findById(any())).willReturn(java.util.Optional.of(comment));
		CommentResponse createComment = commentService.update("내용1", 1L, "이모티콘", mockUser);

		assertEquals(createComment.getContent(), "내용1");
		assertEquals(createComment.getEmoticon(), "이모티콘");
	}

	@Test
	@DisplayName("댓글 작성자와 사용자가 일치하지 않으면 UserNotFoundException 에러 반환")
	public void userNotEqualsThrowUserNotFoundException() {
		User user1 = User.builder()
			.userSeq(1L).username("user1").build();
		User user2 = User.builder()
			.userSeq(2L).username("user2").build();

		Comment comment = Comment.builder()
			.content("내용")
			.userId(user1.getUserSeq())
			.build();

		when(commentRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(comment));

		assertThrows(UserNotFoundException.class, () -> commentService.update("내용", 1L, "emoticon", user2));
		assertThrows(UserNotFoundException.class, () -> commentService.delete(1L, user2));
	}
	@Test
	@DisplayName("channelId를 통해 channel find시 channel 없으면 ResourceNotExistException")
	void boardfindByIdThrowResourceNotExistException() {
		User mockUser = User.builder().userSeq(1L).userId("repo1").build();
		when(boardRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

		assertThrows(ResourceNotExistException.class, () -> commentService.findAllByBoard(1L));
		assertThrows(ResourceNotExistException.class, () -> commentService.create("content",1L,"",mockUser));
	}

	@Test
	@DisplayName("channelId를 통해 channel find시 channel 없으면 ResourceNotExistException")
	void commentfindByIdThrowResourceNotExistException() {
		User mockUser = User.builder().userSeq(1L).userId("repo1").build();
		when(commentRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

		assertThrows(ResourceNotExistException.class, () -> commentService.update("content",1L,"",mockUser));
		assertThrows(ResourceNotExistException.class, () -> commentService.delete(1L,mockUser));
	}
}