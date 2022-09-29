package com.checkmate.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.checkmate.backend.advice.exception.UserNotFoundException;
import com.checkmate.backend.entity.board.Board;
import com.checkmate.backend.entity.channel.Channel;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.BoardDto;
import com.checkmate.backend.model.response.BoardResponse;
import com.checkmate.backend.repo.BoardRepository;
import com.checkmate.backend.repo.ChannelRepository;
import com.checkmate.backend.repo.TeamRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("BoardServiceTest 테스트")
class BoardServiceTest {
	BoardService boardService;
	@Mock
	BoardRepository boardRepository;
	@Mock
	ChannelRepository channelRepository;
	@Mock
	TeamRepository teamRepository;

	@BeforeEach
	void setUp() {
		this.boardService = new BoardService(this.boardRepository, this.channelRepository, this.teamRepository);
	}

	@Test
	void create() {
		User mockUser = User.builder().userId("repo1").build();
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("test")
			.teamDescription("test")
			.build();
		Channel mockChannel = new Channel("채널", mockTeam);
		BoardDto boardDto = new BoardDto("제목", "내용");
		Board board = new Board("제목", "내용", mockUser, mockChannel, mockTeam);

		given(channelRepository.findById(any())).willReturn(java.util.Optional.of(mockChannel));
		given(boardRepository.save(any())).willReturn(board);
		Board createdBoard = boardService.create(1L, boardDto, mockUser);

		assertEquals(board.getTitle(), createdBoard.getTitle());

	}

	@Test
	void modify() {
		User mockUser = User.builder().userId("repo1").build();
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("test")
			.teamDescription("test")
			.build();
		Channel mockChannel = new Channel("채널", mockTeam);
		BoardDto boardDto = new BoardDto("제목수정", "내용수정");
		Board board = new Board("제목", "내용", mockUser, mockChannel, mockTeam);

		given(boardRepository.findById(any())).willReturn(java.util.Optional.of(board));

		BoardResponse response = boardService.modify(1L, boardDto, mockUser);

		assertEquals(response.getTitle(), boardDto.getTitle());
	}

	@Test
	@DisplayName("게시글 작성자와 사용자가 일치하지 않으면 UserNotFoundException 에러 반환")
	public void userNotEqualsThrowUserNotFoundException() {
		User user1 = User.builder()
			.username("user1").build();
		User user2 = User.builder()
			.username("user2").build();

		Board board = Board.builder()
			.content("내용")
			.title("제목")
			.user(user1)
			.build();

		BoardDto boardDto = new BoardDto("제목", "내용");
		when(boardRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(board));

		assertThrows(UserNotFoundException.class, () -> boardService.modify(1L, boardDto, user2));
		assertThrows(UserNotFoundException.class, () -> boardService.delete(1L, user2));
	}

	@Test
	void getBoards() {
		User mockUser = User.builder().userId("repo1").build();
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("test")
			.teamDescription("test")
			.build();
		Channel mockChannel = new Channel("채널", mockTeam);
		Board board = new Board("제목", "내용", mockUser, mockChannel, mockTeam);
		ArrayList<Board> boards = new ArrayList<>();
		boards.add(board);

		given(channelRepository.findById(any())).willReturn(java.util.Optional.of(mockChannel));
		given(boardRepository.findAllByChannel(mockChannel)).willReturn(boards);

		List<BoardResponse> boardResponses = boardService.getBoards(1L);

		assertEquals(boardResponses.get(0).getTitle(), board.getTitle());
	}

	@Test
	void getBoardsByTeam() {
		User mockUser = User.builder().userId("repo1").build();
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("test")
			.teamDescription("test")
			.build();
		Channel mockChannel = new Channel("채널", mockTeam);
		Board board = new Board("제목", "내용", mockUser, mockChannel, mockTeam);
		ArrayList<Board> boards = new ArrayList<>();
		boards.add(board);

		given(teamRepository.findById(any())).willReturn(java.util.Optional.of(mockTeam));
		given(boardRepository.findAllByTeam(mockTeam)).willReturn(boards);

		List<BoardResponse> boardResponses = boardService.getBoardsByTeam(1L);

		assertEquals(boardResponses.get(0).getTitle(), board.getTitle());
	}

	@Test
	void findById() {
		User mockUser = User.builder().userId("repo1").build();
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("test")
			.teamDescription("test")
			.build();
		Channel mockChannel = new Channel("채널", mockTeam);
		Board board = new Board("제목", "내용", mockUser, mockChannel, mockTeam);

		given(boardRepository.findById(any())).willReturn(java.util.Optional.of(board));
		BoardResponse response = boardService.findById(1L);

		assertEquals(response.getTitle(), board.getTitle());
	}
}